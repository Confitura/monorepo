import { Module } from "vuex";
import {
  EmbeddedPresentations,
  EmbeddedUserProfiles,
  Presentation,
  RootState,
  UserProfile,
  VoteStat,
  Voucher
} from "@/types";
import axios from "axios";
import {
  AgendaEntry,
  EmbeddedAgenda,
  EmbeddedRooms,
  EmbeddedTimeSlots,
  Room,
  TimeSlot
} from "@/views/Agenda.vue";

export const LOAD_USERS = "LOAD_USERS";
export const LOAD_SPEAKERS = "LOAD_SPEAKERS";
export const LOAD_SPEAKER = "LOAD_SPEAKER";
export const LOAD_ALL_PRESENTATIONS = "LOAD_ALL_PRESENTATIONS";
export const LOAD_ACCEPTED_PRESENTATIONS = "LOAD_ACCEPTED_PRESENTATIONS";
export const LOAD_VOUCHERS = "LOAD_VOUCHERS";
export const LOAD_INFO = "LOAD_INFO";
export const LOAD_VOTE_STATISTICS = "LOAD_VOTE_STATISTICS";
export const LOAD_TIME_SLOTS = "LOAD_TIME_SLOTS";
export const LOAD_ROOMS = "LOAD_ROOMS";
export const LOAD_SCHEDULE = "LOAD_SCHEDULE";

const SET_USERS = "SET_USERS";
const SET_SPEAKERS = "SET_SPEAKERS";
const SET_SPEAKER = "SET_SPEAKER";
const SET_PRESENTATIONS = "SET_PRESENTATIONS ";
const SET_VOUCHERS = "SET_VOUCHERS";
const SET_INFO = "SET_INFO";
const SET_VOTE_STATISTICS = "SET_VOTE_STATISTICS";
const SET_TIME_SLOTS = "SET_TIME_SLOTS";
const SET_ROOMS = "SET_ROOMS";
const SET_SCHEDULE = "SET_SCHEDULE";

export const adminModule: Module<AdminState, RootState> = {
  state: {
    users: [],
    speakers: [],
    speaker: null,
    presentations: [],
    vouchers: [],
    info: {},
    votes: [],
    rooms: [],
    timeSlots: [],
    schedule: []
  },
  getters: {
    userCount: ({ users }) => users.length,
    presentationCount: ({ presentations }) => presentations.length,
    acceptedPresentationCount: ({ presentations }) =>
      presentations.filter(it => it.status === "accepted").length,
    speakerCount: ({ users }) => users.filter(it => it.speaker).length,
    adminCount: ({ users }) => users.filter(it => it.admin).length,
    volunteerCount: ({ users }) => users.filter(it => it.volunteer).length,
    acceptedSpeakerCount: ({ speakers }) => speakers.length,
    vouchersCount: ({ vouchers }) => vouchers.length,
    info: ({ info }) => info,
    votesCount: ({ votes }) =>
      votes
        .map(it => it.totalVotes)
        .reduce(
          (previousValue, currentValue) => previousValue + currentValue,
          0
        ),
    scheduleMatrix: ({ rooms, timeSlots, schedule }) => {
      const roomIdToIndex = idToIndex(rooms);
      const slotIdToIndex = idToIndex(timeSlots);
      const matrix = createEmptyMatrix(timeSlots, slotIdToIndex, rooms);
      for (const entry of schedule) {
        if (entry.timeSlotId) {
          const slotIndex = slotIdToIndex[entry.timeSlotId];
          if (entry.roomId == null) {
            if (matrix[slotIndex][0] == null) {
              matrix[slotIndex][0] = entry;
            } else {
              console.warn("conflict in agenda. Two entries in same slot");
            }
          } else {
            const roomIndex = roomIdToIndex[entry.roomId];
            matrix[slotIndex][roomIndex] = entry;
          }
        }
      }
      return matrix;
    },
    availablePresentations: ({ presentations }) => {
      return presentations.filter(it => it.status === "accepted");
    }
  },
  mutations: {
    [SET_USERS](store, payload: { users: UserProfile[] }) {
      store.users = payload.users.sort((a, b) =>
        (a.name || "") > (b.name || "") ? 1 : -1
      );
    },
    [SET_VOUCHERS](store, payload: { vouchers: Voucher[] }) {
      store.vouchers = payload.vouchers;
    },
    [SET_SPEAKERS](store, payload: { speakers: UserProfile[] }) {
      store.speakers = payload.speakers;
    },
    [SET_SPEAKER](store, payload: { speaker: UserProfile }) {
      store.speaker = payload.speaker;
    },
    [SET_PRESENTATIONS](store, payload: { presentations: Presentation[] }) {
      store.presentations = payload.presentations.sort((a, b) =>
        a.title > b.title ? 1 : -1
      );
    },
    [SET_INFO](store, payload: { info: Info }) {
      store.info = payload.info;
    },
    [SET_VOTE_STATISTICS](store, payload: { votes: VoteStat[] }) {
      store.votes = payload.votes;
    },
    [SET_ROOMS](store, payload: { rooms: Room[] }) {
      store.rooms = payload.rooms;
    },
    [SET_TIME_SLOTS](store, payload: { timeSlots: TimeSlot[] }) {
      store.timeSlots = payload.timeSlots;
    },
    [SET_SCHEDULE](store, payload: { schedule: AgendaEntry[] }) {
      store.schedule = payload.schedule;
    }
  },
  actions: {
    [LOAD_USERS]({ commit }) {
      return axios.get<EmbeddedUserProfiles>("/api/users").then(it => {
        commit(SET_USERS, {
          users: it.data._embedded.publicUsers || it.data._embedded.users
        });
      });
    },
    [LOAD_VOUCHERS]({ commit }) {
      return axios.get<Voucher[]>("/api/vouchers").then(it => {
        commit(SET_VOUCHERS, { vouchers: it.data });
      });
    },
    [LOAD_SPEAKERS]({ commit }) {
      return axios
        .get<EmbeddedUserProfiles>("/api/users/search/speakers")
        .then(it => {
          commit(SET_SPEAKERS, { speakers: it.data._embedded.publicUsers });
        });
    },
    [LOAD_SPEAKER]({ commit }, { id }) {
      return axios.get<UserProfile>(`/api/users/${id}/public`).then(it => {
        commit(SET_SPEAKER, { speaker: it.data });
      });
    },
    [LOAD_ALL_PRESENTATIONS]({ commit }) {
      return axios
        .get<EmbeddedPresentations>("/api/presentations", {
          params: { projection: "inlineSpeaker" }
        })
        .then(it => {
          commit(SET_PRESENTATIONS, {
            presentations: it.data._embedded.presentations
          });
        });
    },
    [LOAD_ACCEPTED_PRESENTATIONS]({ commit }) {
      return axios
        .get<EmbeddedPresentations>("/api/presentations/search/accepted", {
          params: { projection: "inlineSpeaker" }
        })
        .then(it => it.data._embedded.presentations)
        .then(presentations => {
          commit(SET_PRESENTATIONS, { presentations });
        });
    },
    [LOAD_INFO]({ commit }) {
      return axios
        .get<Info>("/api/api/actuator/info")
        .then(it => it.data)
        .then(info => {
          commit(SET_INFO, { info });
        });
    },
    [LOAD_VOTE_STATISTICS]({ commit }) {
      return axios
        .get<VoteStat[]>("/api/votes/statistics")
        .then(it => it.data)
        .then(votes => {
          commit(SET_VOTE_STATISTICS, { votes });
        });
    },
    [LOAD_ROOMS]({ commit }) {
      return axios
        .get<EmbeddedRooms>("/api/rooms")
        .then(it => it.data._embedded.rooms)
        .then(rooms => {
          commit(SET_ROOMS, { rooms });
        });
    },
    [LOAD_TIME_SLOTS]({ commit }) {
      return axios
        .get<EmbeddedTimeSlots>("/api/time-slots")
        .then(it => it.data._embedded.timeSlots)
        .then(timeSlots => {
          commit(SET_TIME_SLOTS, { timeSlots });
        });
    },
    [LOAD_SCHEDULE]({ commit }) {
      return axios
        .get<EmbeddedAgenda>("/api/agenda")
        .then(it => it.data._embedded.agendaEntries)
        .then(schedule => {
          commit(SET_SCHEDULE, { schedule });
        });
    }
  }
};

export interface AdminState {
  users: UserProfile[];
  speakers: UserProfile[];
  presentations: Presentation[];
  speaker: UserProfile | null;
  vouchers: Voucher[];
  info: Info;
  votes: VoteStat[];
  rooms: Room[];
  timeSlots: TimeSlot[];
  schedule: AgendaEntry[];
}

interface Info {
  c4p?: {
    start: number;
    end: number;
    enabled: boolean;
  };
}

function idToIndex(entries: { id: string }[]) {
  const roomIdToIndex: { [id: string]: number } = {};
  let index = 0;
  for (const entry of entries) {
    roomIdToIndex[entry.id] = index++;
  }
  return roomIdToIndex;
}

function createEmptyMatrix(slots: any, slotIdToIndex: any, rooms: any) {
  const matrix = new Array(slots.length);
  for (const slot of slots) {
    const slotIndex = slotIdToIndex[slot.id];
    if (slot.forAllRooms) {
      matrix[slotIndex] = new Array(1);
    } else {
      matrix[slotIndex] = new Array(rooms.length);
    }
  }
  return matrix;
}
