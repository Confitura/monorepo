import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'

// jsdom doesn't implement ResizeObserver, required by Vuetify's tables/windows
global.ResizeObserver = class ResizeObserver {
  observe() {}
  unobserve() {}
  disconnect() {}
}
import { createVuetify } from 'vuetify'
import { createTestingPinia } from '@pinia/testing'
import AgendaEditor from '@/components/admin/agenda-editor.vue'
import { useAgendaStore } from '@/stores/agenda'
import { useNotificationStore } from '@/stores/notification'

const DAY_ID = 'day-1'

const day = { id: DAY_ID, label: 'Day 1', date: '2026-09-19', displayOrder: 1 }
const timeSlots = [
  { dayId: DAY_ID, displayOrder: 0, label: '09:00 - 10:00', start: '09:00', end: '10:00', forAllRooms: false },
  { dayId: DAY_ID, displayOrder: 1, label: '10:15 - 11:15', start: '10:15', end: '11:15', forAllRooms: true },
]
const rooms = [
  { id: 'room-ab', label: 'AB', displayOrder: 1 },
  { id: 'room-cde', label: 'CDE', displayOrder: 2 },
]
const entries = [
  {
    id: 'entry-1',
    dayId: DAY_ID,
    timeSlotIndex: 0,
    roomId: 'room-ab',
    roomLabel: 'AB',
    label: null,
    presentationId: 'presentation-1',
    timeSlotSpan: 1,
    timeSlotLabel: '09:00 - 10:00',
  },
]
const presentations = [
  { id: 'presentation-1', title: 'Deep Dive Into Records', flatSpeakers: 'Ada Lovelace' },
]

const { agendaApi, daysApi, presentationApi } = vi.hoisted(() => ({
  agendaApi: {
    getAllTimeSlots: vi.fn(),
    getAllRooms1: vi.fn(),
    getAgendaEntriesByDay: vi.fn(),
    createTimeSlot: vi.fn(),
    updateTimeSlot: vi.fn(),
    deleteTimeSlot: vi.fn(),
    createRoom: vi.fn(),
    updateRoom: vi.fn(),
    removeRoom: vi.fn(),
    saveAgendaEntry: vi.fn(),
    updateAgendaEntry: vi.fn(),
    deleteAgendaEntry: vi.fn(),
    moveAgendaEntry: vi.fn(),
  },
  daysApi: { getDayById: vi.fn() },
  presentationApi: { getAllPresentations: vi.fn() },
}))

vi.mock('@/utils/api', () => ({ agendaApi, daysApi, presentationApi }))

let wrapper: ReturnType<typeof mount> | null = null
let container: HTMLElement | null = null

async function mountEditor() {
  container = document.createElement('div')
  document.body.appendChild(container)
  wrapper = mount(AgendaEditor, {
    attachTo: container,
    props: { dayId: DAY_ID },
    global: {
      plugins: [createVuetify(), createTestingPinia({ stubActions: false })],
    },
  })
  await flushPromises()
  return wrapper
}

async function clickButtonWithText(editor: ReturnType<typeof mount>, text: string) {
  const button = editor.findAll('button').find((it) => it.text() === text)
  if (!button) throw new Error(`No "${text}" button found`)
  await button.trigger('click')
  await flushPromises()
}

function dialogField(label: string) {
  const field = [...document.querySelectorAll('.v-overlay .v-input')].find((it) =>
    it.textContent?.includes(label),
  )
  if (!field) throw new Error(`No "${label}" field found in the open dialog`)
  return field.querySelector('input')!
}

async function fillDialogField(label: string, value: string) {
  const input = dialogField(label)
  input.value = value
  input.dispatchEvent(new Event('input', { bubbles: true }))
  await flushPromises()
}

async function clickDialogButtonContaining(text: string) {
  const button = [...document.querySelectorAll('.v-overlay button')].find((it) =>
    it.textContent?.includes(text),
  )
  if (!button) throw new Error(`No button containing "${text}" found in the open dialog`)
  button.dispatchEvent(new MouseEvent('click', { bubbles: true }))
  await flushPromises()
}

async function selectDialogOption(label: string, option: string) {
  const select = [...document.querySelectorAll('.v-overlay .v-input')].find((it) =>
    it.textContent?.includes(label),
  )
  if (!select) throw new Error(`No "${label}" select found in the open dialog`)
  select.querySelector('input')!.dispatchEvent(new MouseEvent('mousedown', { bubbles: true }))
  await flushPromises()
  const item = [...document.querySelectorAll('.v-overlay .v-list-item')].find(
    (it) => it.textContent?.trim() === option,
  )
  if (!item) throw new Error(`No "${option}" option found for "${label}"`)
  item.dispatchEvent(new MouseEvent('click', { bubbles: true }))
  await flushPromises()
}

// dialogs are teleported out of the component, so they are looked up in the document
function dialogButton(text: string) {
  const button = [...document.querySelectorAll<HTMLButtonElement>('.v-overlay button')].find(
    (it) => it.textContent?.trim() === text,
  )
  if (!button) throw new Error(`No "${text}" button found in the open dialog`)
  return button
}

async function clickDialogButton(text: string) {
  dialogButton(text).dispatchEvent(new MouseEvent('click', { bubbles: true }))
  await flushPromises()
}

describe('component agenda-editor.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    daysApi.getDayById.mockResolvedValue({ data: day })
    agendaApi.getAllTimeSlots.mockResolvedValue({ data: timeSlots })
    agendaApi.getAllRooms1.mockResolvedValue({ data: rooms })
    agendaApi.getAgendaEntriesByDay.mockResolvedValue({ data: entries })
    presentationApi.getAllPresentations.mockResolvedValue({ data: presentations })
    Object.values(agendaApi)
      .filter((fn) => !fn.getMockImplementation())
      .forEach((fn) => fn.mockResolvedValue({ data: {} }))
  })

  afterEach(() => {
    wrapper?.unmount()
    container?.remove()
  })

  it('renders a row per time slot and a column per room', async () => {
    const editor = await mountEditor()

    const rows = editor.findAll('tbody tr')
    expect(rows).toHaveLength(timeSlots.length)
    expect(rows[0].text()).toContain('09:00 - 10:00')
    expect(rows[1].text()).toContain('10:15 - 11:15')

    const headers = editor.findAll('thead th')
    // time + rooms + all rooms
    expect(headers).toHaveLength(rooms.length + 2)
    expect(headers[1].text()).toContain('AB')
    expect(headers[2].text()).toContain('CDE')
  })

  it('only offers the all-rooms cell on a slot spanning all rooms', async () => {
    const editor = await mountEditor()

    const addButton = (row: number, cell: number) =>
      editor.findAll('tbody tr')[row].findAll('td')[cell].find('button')
        .element as HTMLButtonElement

    // second slot spans all rooms: only its all-rooms cell can be filled
    expect(addButton(1, 1).disabled).toBe(true)
    expect(addButton(1, 2).disabled).toBe(true)
    expect(addButton(1, rooms.length + 1).disabled).toBe(false)

    // first slot does not, so the rooms take entries and the all-rooms cell does not
    expect(addButton(0, 2).disabled).toBe(false)
    expect(addButton(0, rooms.length + 1).disabled).toBe(true)
  })

  it('shows the presentation scheduled in a slot', async () => {
    const editor = await mountEditor()

    const cells = editor.findAll('tbody tr')[0].findAll('td')
    expect(cells[1].text()).toContain('Deep Dive Into Records')
    expect(cells[1].text()).toContain('Ada Lovelace')
    // free slots offer adding an entry
    expect(cells[2].text()).toContain('Add')
  })

  it('adds a time slot starting when the last one ends', async () => {
    const editor = await mountEditor()

    await clickButtonWithText(editor, 'Time Slot')
    await clickDialogButton('Save')

    expect(agendaApi.createTimeSlot).toHaveBeenCalledWith(DAY_ID, {
      start: '11:15',
      end: '12:15',
      forAllRooms: false,
    })
    // the day is reloaded so the new slot shows up in its chronological place
    expect(agendaApi.getAllTimeSlots).toHaveBeenCalledTimes(2)
  })

  it('derives the end time from the duration', async () => {
    const editor = await mountEditor()

    await clickButtonWithText(editor, 'Time Slot')
    expect(dialogField('Duration').value).toBe('60')
    expect(dialogField('End Time').value).toBe('12:15')
    // a prefilled slot is savable straight away, no field has to be touched
    expect(dialogButton('Save').disabled).toBe(false)

    await fillDialogField('Duration', '45')
    expect(dialogField('End Time').value).toBe('12:00')

    await clickDialogButton('Save')
    expect(agendaApi.createTimeSlot).toHaveBeenCalledWith(DAY_ID, {
      start: '11:15',
      end: '12:00',
      forAllRooms: false,
    })
  })

  it('derives the duration from a hand-picked end time', async () => {
    const editor = await mountEditor()

    await clickButtonWithText(editor, 'Time Slot')
    await fillDialogField('End Time', '11:45')

    expect(dialogField('Duration').value).toBe('30')
  })

  it('starts a slot when the previous one ends', async () => {
    const editor = await mountEditor()

    // edit the second slot (10:15 - 11:15), which follows one ending at 10:00
    const [editButton] = editor.findAll('tbody tr')[1].findAll('td')[0].findAll('button')
    await editButton.trigger('click')
    await clickDialogButtonContaining('Start when previous slot ends (10:00)')

    expect(dialogField('Start Time').value).toBe('10:00')
    // the 60 minute duration is kept, so the end time follows the start
    expect(dialogField('End Time').value).toBe('11:00')

    await clickDialogButton('Save')
    expect(agendaApi.updateTimeSlot).toHaveBeenCalledWith(DAY_ID, 1, {
      start: '10:00',
      end: '11:00',
      forAllRooms: true,
    })
  })

  it('keeps the dialog open for the next slot when adding several in a row', async () => {
    const editor = await mountEditor()

    await clickButtonWithText(editor, 'Time Slot')
    await fillDialogField('Duration', '30')
    await clickDialogButton('Save & add next')

    // ready for the following slot, starting where the saved one ended
    expect(dialogField('Start Time').value).toBe('11:45')
    expect(dialogField('End Time').value).toBe('12:15')

    await clickDialogButton('Save')

    expect(agendaApi.createTimeSlot).toHaveBeenNthCalledWith(1, DAY_ID, {
      start: '11:15',
      end: '11:45',
      forAllRooms: false,
    })
    expect(agendaApi.createTimeSlot).toHaveBeenNthCalledWith(2, DAY_ID, {
      start: '11:45',
      end: '12:15',
      forAllRooms: false,
    })
  })

  it('refuses to save a slot ending before it starts', async () => {
    const editor = await mountEditor()

    await clickButtonWithText(editor, 'Time Slot')
    await fillDialogField('End Time', '10:00')

    expect(document.body.textContent).toContain('End time must be after the start time')
    expect(dialogButton('Save').disabled).toBe(true)

    await clickDialogButton('Save')
    expect(agendaApi.createTimeSlot).not.toHaveBeenCalled()
  })

  it('adds an agenda entry to an empty cell', async () => {
    const editor = await mountEditor()

    // second room of the first time slot is free
    const cell = editor.findAll('tbody tr')[0].findAll('td')[2]
    await cell.find('button').trigger('click')
    await clickDialogButton('Save')

    expect(agendaApi.saveAgendaEntry).toHaveBeenCalledWith({
      dayId: DAY_ID,
      timeSlotIndex: 0,
      roomId: 'room-cde',
      label: '',
      presentationId: '',
    })
  })

  it('deletes a time slot with its entries once confirmed', async () => {
    const editor = await mountEditor()

    const [, deleteButton] = editor.findAll('tbody tr')[0].findAll('td')[0].findAll('button')
    await deleteButton.trigger('click')
    await flushPromises()

    expect(document.body.textContent).toContain('DELETE time slot 09:00 - 10:00 with 1 entry')

    await clickDialogButton('Confirm')

    expect(agendaApi.deleteTimeSlot).toHaveBeenCalledWith(DAY_ID, 0)
    expect(agendaApi.getAgendaEntriesByDay).toHaveBeenCalledTimes(2)
  })

  it('does not delete a time slot when the confirmation is cancelled', async () => {
    const editor = await mountEditor()

    const [, deleteButton] = editor.findAll('tbody tr')[1].findAll('td')[0].findAll('button')
    await deleteButton.trigger('click')
    await flushPromises()

    await clickDialogButton('Cancel')

    expect(agendaApi.deleteTimeSlot).not.toHaveBeenCalled()
  })

  it('refuses to move an entry onto an occupied slot', async () => {
    const editor = await mountEditor()

    // the free CDE cell of the first slot...
    const cell = editor.findAll('tbody tr')[0].findAll('td')[2]
    await cell.find('button').trigger('click')
    // ...moved onto AB, which already holds a presentation
    await selectDialogOption('Room', 'AB')
    await clickDialogButton('Save')

    expect(agendaApi.saveAgendaEntry).not.toHaveBeenCalled()
    expect(useNotificationStore().notifications.map((it) => it.text)).toContain(
      'There is already an entry in that time slot and room',
    )
  })

  it('creates and removes rooms', async () => {
    await mountEditor()
    const store = useAgendaStore()

    await store.createRoom(DAY_ID, { label: 'Room 13' })
    expect(agendaApi.createRoom).toHaveBeenCalledWith(DAY_ID, { label: 'Room 13' })

    await store.deleteRoom(DAY_ID, 'room-cde')
    expect(agendaApi.removeRoom).toHaveBeenCalledWith('room-cde')
  })

  it('swaps display order of neighbouring rooms when moving a room', async () => {
    await mountEditor()
    const store = useAgendaStore()

    await store.moveRoom(DAY_ID, 'room-cde', -1)

    expect(agendaApi.updateRoom).toHaveBeenNthCalledWith(1, 'room-cde', { displayOrder: 1 })
    expect(agendaApi.updateRoom).toHaveBeenNthCalledWith(2, 'room-ab', { displayOrder: 2 })
  })

  it('does not move the first room to the left', async () => {
    await mountEditor()
    const store = useAgendaStore()

    await store.moveRoom(DAY_ID, 'room-ab', -1)

    expect(agendaApi.updateRoom).not.toHaveBeenCalled()
  })

  it('moves an agenda entry to another slot', async () => {
    await mountEditor()
    const store = useAgendaStore()

    await store.moveAgendaEntry('entry-1', { dayId: DAY_ID, timeSlotIndex: 1, roomId: 'room-cde' }, DAY_ID)

    expect(agendaApi.moveAgendaEntry).toHaveBeenCalledWith('entry-1', {
      dayId: DAY_ID,
      timeSlotIndex: 1,
      roomId: 'room-cde',
    })
  })
})
