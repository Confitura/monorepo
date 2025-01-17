import { defineStore } from 'pinia'

export const useThemeStore = defineStore('theme', {
  state: () => ({
    headerTheme: 'default',
    headerHeight: 73,
    windowWidth: 0
  }),
  getters: {
    isSm: state => state.windowWidth >= 576,
    isMd: state => state.windowWidth >= 768,
    isLg: state => state.windowWidth >= 992,
    isXl: state => state.windowWidth >= 1200
  },
  actions: {
    changeHeaderTheme(theme: { color: string }) {
      this.headerTheme = theme.color
    },
    resizeWindow(size: { width: number }) {
      this.windowWidth = size.width
      if (size.width >= 992) {
        this.headerHeight = 73
      } else {
        this.headerHeight = 60
      }
    }
  }
})