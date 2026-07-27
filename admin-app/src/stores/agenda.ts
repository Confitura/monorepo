import { defineStore } from 'pinia'
import { daysApi, agendaApi, presentationApi } from '@/utils/api'
import type {
  AssignAgendaEntryRequest,
  CreateRoomRequest,
  CreateTimeSlotRequest,
  FullPresentation,
  InlineAgendaEntry,
  InlineDay,
  InlineRoom,
  InlineTimeSlot,
  UpdateAgendaEntrySlotRequest,
  UpdateRoomRequest,
  UpdateTimeSlotRequest,
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
    entriesForDay: (state) => (dayId: string) => state.agendaEntriesByDay[dayId] || [],
  },
  actions: {
    async refreshData(dayIds: string | string[]) {
      try {
        const ids = Array.isArray(dayIds) ? [...new Set(dayIds)] : [dayIds]

        // Load presentations at once (global)
        const presentationsResponse = await presentationApi.getAllPresentations()

        this.presentations = presentationsResponse.data.sort((a, b) =>
          a.title.localeCompare(b.title)
        )

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
    async moveAgendaEntry(id: string, request: UpdateAgendaEntrySlotRequest, dayId: string) {
      try {
        await agendaApi.moveAgendaEntry(id, request)
        await this.refreshData(dayId)
      } catch (e) {
        console.error('AgendaStore.moveAgendaEntry error', e)
        throw e
      }
    },
    async createTimeSlot(dayId: string, request: CreateTimeSlotRequest) {
      try {
        await agendaApi.createTimeSlot(dayId, request)
        await this.refreshData(dayId)
      } catch (e) {
        console.error('AgendaStore.createTimeSlot error', e)
        throw e
      }
    },
    async updateTimeSlot(dayId: string, displayOrder: number, request: UpdateTimeSlotRequest) {
      try {
        await agendaApi.updateTimeSlot(dayId, displayOrder, request)
        await this.refreshData(dayId)
      } catch (e) {
        console.error('AgendaStore.updateTimeSlot error', e)
        throw e
      }
    },
    async deleteTimeSlot(dayId: string, displayOrder: number) {
      try {
        await agendaApi.deleteTimeSlot(dayId, displayOrder)
        await this.refreshData(dayId)
      } catch (e) {
        console.error('AgendaStore.deleteTimeSlot error', e)
        throw e
      }
    },
    async createRoom(dayId: string, request: CreateRoomRequest) {
      try {
        await agendaApi.createRoom(dayId, request)
        await this.refreshData(dayId)
      } catch (e) {
        console.error('AgendaStore.createRoom error', e)
        throw e
      }
    },
    async updateRoom(dayId: string, id: string, request: UpdateRoomRequest) {
      try {
        await agendaApi.updateRoom(id, request)
        await this.refreshData(dayId)
      } catch (e) {
        console.error('AgendaStore.updateRoom error', e)
        throw e
      }
    },
    async deleteRoom(dayId: string, id: string) {
      try {
        await agendaApi.removeRoom(id)
        await this.refreshData(dayId)
      } catch (e) {
        console.error('AgendaStore.deleteRoom error', e)
        throw e
      }
    },
    /**
     * Swaps the display order of a room with its neighbour, moving it left or right in the agenda.
     */
    async moveRoom(dayId: string, id: string, direction: -1 | 1) {
      const rooms = this.roomsForDay(dayId)
      const index = rooms.findIndex((room) => room.id === id)
      const neighbour = rooms[index + direction]
      if (index === -1 || !neighbour) return
      const room = rooms[index]
      try {
        await agendaApi.updateRoom(room.id, { displayOrder: neighbour.displayOrder })
        await agendaApi.updateRoom(neighbour.id, { displayOrder: room.displayOrder })
        await this.refreshData(dayId)
      } catch (e) {
        console.error('AgendaStore.moveRoom error', e)
        throw e
      }
    },
  },
})
