// jsdom does not implement visualViewport, required by Vuetify's v-dialog
const visualViewportMock = Object.assign(new EventTarget(), {
  width: 1024,
  height: 768,
  offsetTop: 0,
  offsetLeft: 0,
  scale: 1,
  pageTop: 0,
  pageLeft: 0,
})
Object.defineProperty(window, 'visualViewport', { value: visualViewportMock, writable: true })

// md-editor-v3 starts setTimeouts that fire after jsdom is torn down ("document is not defined");
// no spec drives the editor itself (they set form fields through the page), so stub it for all of them.
vi.mock('md-editor-v3', () => ({ MdEditor: { name: 'MdEditor', template: '<div class="stub-md-editor" />' } }))
