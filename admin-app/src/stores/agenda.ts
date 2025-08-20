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
    presentations: [] as FullPresentation[],

    // New multi-day support
    days: [] as InlineDay[],
    timeSlotsByDay: {} as Record<string, InlineTimeSlot[]>,
    roomsByDay: {} as Record<string, InlineRoom[]>,
    agendaEntriesByDay: {} as Record<string, InlineAgendaEntry[]>,
  }),
  getters: {
    // New helpers for multi-day access
    timeSlotsForDay: (state) => (dayId: string) => state.timeSlotsByDay[dayId] || [],
    roomsForDay: (state) => (dayId: string) => state.roomsByDay[dayId] || [],
  },
  actions: {
    async refreshData(dayIds: string | string[]) {
      try {
        const ids = Array.isArray(dayIds) ? [...new Set(dayIds)] : [dayIds]

        // Load presentations once (global)
        const presentationsResponse = await presentationApi.getAllPresentations()
        this.presentations = presentationsResponse.data

        // Fetch all requested days in parallel
        const results = await Promise.all(
          ids.map(async (dayId) => {
            const [dayResponse, timeSlotsResponse, roomsResponse, agendaEntriesResponse] = await Promise.all([
              daysApi.getDayById(dayId),
              agendaApi.getAllTimeSlots(dayId),
              agendaApi.getAllRooms1(dayId),
              agendaApi.getAgendaEntriesByDay(dayId),
            ])

            return {
              day: dayResponse.data as InlineDay,
              timeSlots: timeSlotsResponse.data as InlineTimeSlot[],
              rooms: roomsResponse.data as InlineRoom[],
              agendaEntries: agendaEntriesResponse.data as InlineAgendaEntry[],
            }
          }),
        )

        // Update maps and arrays
        const fetchedDays: InlineDay[] = []
        for (const r of results) {
          const dayId = (r.day as any).id as string
          fetchedDays.push(r.day)
          this.timeSlotsByDay[dayId] = r.timeSlots
          this.roomsByDay[dayId] = r.rooms
          this.agendaEntriesByDay[dayId] = r.agendaEntries
        }
        this.days = fetchedDays

      } catch (e) {
        console.error('AgendaStore.refreshData error', e)
        throw e
      }
    },
    getPresentation(id: string | null) {
      if (!id) return null
      return this.presentations.find((p) => p.id === id) || null
    },
    getAgendaEntry(timeSlot: InlineTimeSlot, room: InlineRoom | null, dayId: string) {
      const timeSlotIndex = (timeSlot as any).displayOrder
      const roomId = room?.id || null
      const source = this.agendaEntriesByDay[dayId] || []
      return (
        source.find(
          (entry) => entry.timeSlotIndex === timeSlotIndex && entry.roomId === roomId,
        ) || null
      )
    },
    findPresentation(timeSlot: InlineTimeSlot, room: InlineRoom | null, dayId: string) {
      const entry = this.getAgendaEntry(timeSlot, room, dayId)
      if (!entry || !entry.presentationId) return null
      return this.getPresentation(entry.presentationId)
    },
    async saveAgendaEntry(request: AssignAgendaEntryRequest, dayId: string) {
      try {
        if (!request.dayId) {
          request.dayId = dayId
        }
        await agendaApi.saveAgendaEntry(request)
        await this.refreshData(dayId)
      } catch (e) {
        console.error('AgendaStore.saveAgendaEntry error', e)
        throw e
      }
    },
    async updateAgendaEntry(
      id: string,
      request: { label?: string; presentationId?: string; roomId?: string },
      dayId: string,
    ) {
      try {
        await agendaApi.updateAgendaEntry(id, request)
        await this.refreshData(dayId)
      } catch (e) {
        console.error('AgendaStore.updateAgendaEntry error', e)
        throw e
      }
    },
    async deleteAgendaEntry(id: string, dayId: string) {
      try {
        await agendaApi.deleteAgendaEntry(id)
        await this.refreshData(dayId)
      } catch (e) {
        console.error('AgendaStore.deleteAgendaEntry error', e)
        throw e
      }
    },
  },
})
