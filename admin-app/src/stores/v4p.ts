import {defineStore} from 'pinia'
import type {InlineVote} from "@/utils/api";

export const useV4PStore = defineStore('v4p', {
  state: () => {
    const currentPosition: number = -1;
    const votes: InlineVote[] = []
    return {
      currentPosition, votes
    }
  },
  getters: {
    currentVote(state) {
      if (state.currentPosition < 0 || state.currentPosition >= state.votes.length) {
        return null;
      } else {
        return state.votes[state.currentPosition]
      }
    }
  }
})
