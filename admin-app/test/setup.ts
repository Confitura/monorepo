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
