import {defineStore} from 'pinia'
import type {InlineVote} from "@/utils/api-axios-client";

export const useV4PStore = defineStore('v4p', {
  state: () => {
    let currentPosition: number = -1;
    let votes: InlineVote[] = []
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
