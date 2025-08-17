import { defineStore } from 'pinia'
import { daysApi, agendaApi, presentationApi } from '@/utils/api'
import type {
  AssignAgendaEntryRequest,
  FullPresentation,
  InlineAgendaEntry,
  InlineDay,
  InlineRoom,
  InlineTimeSlot,
} from '@/utils/api-axios-client'

export const useAgendaStore = defineStore('agenda', {
  state: () => ({
    day: null as InlineDay | null,
    timeSlots: [] as InlineTimeSlot[],
    rooms: [] as InlineRoom[],
    presentations: [] as FullPresentation[],
    agendaEntries: [] as InlineAgendaEntry[],
  }),
  getters: {
    sortedTimeSlots: (state) => state.timeSlots,
    sortedRooms: (state) => state.rooms,
  },
  actions: {
    async refreshData(dayId: string) {
      try {
        const dayResponse = await daysApi.getDayById(dayId)
        this.day = dayResponse.data

        const timeSlotsResponse = await agendaApi.getAllTimeSlots(dayId)
        this.timeSlots = timeSlotsResponse.data

        const roomsResponse = await agendaApi.getAllRooms1(dayId)
        this.rooms = roomsResponse.data

        const presentationsResponse = await presentationApi.getAllPresentations()
        this.presentations = presentationsResponse.data

        const agendaEntriesResponse = await agendaApi.getAgendaEntriesByDay(dayId)
        this.agendaEntries = agendaEntriesResponse.data
      } catch (e) {
        console.error('AgendaStore.refreshData error', e)
        throw e
      }
    },
    getPresentation(id: string | null) {
      if (!id) return null
      return this.presentations.find((p) => p.id === id) || null
    },
    getAgendaEntry(timeSlot: InlineTimeSlot, room: InlineRoom | null) {
      const timeSlotIndex = timeSlot.displayOrder
      const roomId = room?.id || null
      return (
        this.agendaEntries.find(
          (entry) => entry.timeSlotIndex === timeSlotIndex && entry.roomId === roomId,
        ) || null
      )
    },
    findPresentation(timeSlot: InlineTimeSlot, room: InlineRoom | null) {
      const entry = this.getAgendaEntry(timeSlot, room)
      if (!entry || !entry.presentationId) return null
      return this.getPresentation(entry.presentationId)
    },
    async saveAgendaEntry(request: AssignAgendaEntryRequest, dayId?: string) {
      try {
        if (!request.dayId && dayId) {
          request.dayId = dayId
        }
        await agendaApi.saveAgendaEntry(request)
        await this.refreshData(request.dayId || dayId!)
      } catch (e) {
        console.error('AgendaStore.saveAgendaEntry error', e)
        throw e
      }
    },
    async updateAgendaEntry(id: string, request: { label?: string; presentationId?: string; roomId?: string }, dayId?: string) {
      try {
        await agendaApi.updateAgendaEntry(id, request)
        const refreshDayId = dayId || this.day?.id
        if (refreshDayId) {
          await this.refreshData(refreshDayId)
        }
      } catch (e) {
        console.error('AgendaStore.updateAgendaEntry error', e)
        throw e
      }
    },
    async deleteAgendaEntry(id: string, dayId?: string) {
      try {
        await agendaApi.deleteAgendaEntry(id)
        const refreshDayId = dayId || this.day?.id
        if (refreshDayId) {
          await this.refreshData(refreshDayId)
        }
      } catch (e) {
        console.error('AgendaStore.deleteAgendaEntry error', e)
        throw e
      }
    },
  },
})
