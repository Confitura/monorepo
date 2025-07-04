<script setup lang="ts">
import {voteForPapersApi} from "@/utils/api.ts";
import {v4 as uuidv4} from 'uuid';
import type {InlineVote, InlineVoteSpeaker} from "@/utils/api-axios-client";
import {useV4PStore} from "@/stores/v4p.ts";

definePage({
  meta: {
    icon: 'mdi-home',
    title: 'Vote for papers',
    drawerIndex: 0,
    skipMenu: true,
    layout: 'no-distractions',
  },
})

const isMobile = ref(window.innerWidth < 600)

const store = useV4PStore()
const {votes, currentPosition} = storeToRefs(store);
const currentVote = computed(() => store.currentVote)

const showShort = ref(true);
const showSpeaker: Ref<InlineVoteSpeaker | null> = ref(null);

function showBio(speaker: InlineVoteSpeaker | null) {
  showSpeaker.value = speaker;
}

function getV4Ptoken(): string {
  let token = localStorage.getItem('v4p-token')
  if (!token) {
    localStorage.setItem('v4p-token', uuidv4())
    return localStorage.getItem('v4p-token')!!
  }
  return token;
}

async function startVoting() {
  let token = getV4Ptoken();
  let result = await voteForPapersApi.start(token)
  votes.value = result.data
  currentPosition.value = 0;
}


onMounted(async () => {
  document.addEventListener('keyup', doc_keyUp, false);
})

onUnmounted(() => {
  document.removeEventListener('keyup', doc_keyUp, false);
})

function doc_keyUp(e: any) {

  if (currentVote.value) {
    if (e.code === 'ArrowDown' || e.code === 'KeyS') {
      let rate = currentVote.value.rate || 0;
      if (rate > -1) {
        currentVote.value.rate = rate - 1;
      }
    }
    if (e.code === 'ArrowUp' || e.code === 'KeyW') {
      let rate = currentVote.value.rate || 0;
      if (rate < 1) {
        currentVote.value.rate = rate + 1;
      }
    }
    if (e.code === 'ArrowRight' || e.code === 'KeyD') {
      vote(currentVote.value, currentVote.value.rate || 0)
    }
    if (e.code === 'ArrowLeft' || e.code === 'KeyA') {
      currentPosition.value -= 1;
    }
    if (e.code === 'Space') {
      showShort.value = !showShort.value;
    }
  } else {
    if (e.code === 'Enter' || e.code === 'ArrowRight' || e.code === 'KeyD') {
      startVoting()
    }
  }
}

function toggleDescription() {
  showShort.value = !showShort.value;
}

async function vote(vote: InlineVote, value: number) {
  vote.rate = value;
  await voteForPapersApi.save(vote)
  currentPosition.value += 1;
}
</script>

<template>
  <div class="content">

    <v-container height="100vh">
      <div v-if="currentPosition==-1">
        <div class="intro">
          <h2 class="text-h4">Vote 4 Papers</h2>

          <div class="text-body-1	">
            The moment you were waiting for is here! Vote 4 Papers is open.
            You
            have around 60 presentations to choose from.
          </div>

          <div class="text-body-1">
            For each and every presentation you can either say that you are
            very
            interested in seeing it (+1), not interested at all (-1), or you
            don't mind it (0).
          </div>

          <div class="text-body-1	">
            Since we know that you are very busy, we have asked our speakers
            to
            provide TL;DR version of the description of their presentations
            (max
            300 characters). If it's not enough for you to make a decision you
            can switch to full description.

          </div>

          <div class="text-body-1	">
            <p>We are waiting for your votes till end of Wednesday, June 1st</p>

          </div>
          <div>
            <v-btn color="teal-accent-4" block @click="startVoting()">Start
            </v-btn>
          </div>
          <v-divider></v-divider>
          <div class="text-body-1	" v-if="!isMobile">
            <p>
              btw. you can also vote with keyboard shortcuts.
            </p>
            <div><pre>
shortcuts: <br/>

enter     -&gt; start<br/>
space     -&gt; toggle description
?         -&gt; self

w | up    -&gt; +1
s | down  -&gt; -1
d | right -&gt; next
a | left  -&gt; go back
</pre>
            </div>

          </div>
        </div>
      </div>
      <div v-if="currentPosition>=votes?.length">
        <div class="intro">
          <h2 class="v4p__header">Thank you!</h2>
          <div class="v4p_summary">
            <p>
              For going through all presentations. We know it was not easy :)
            </p>
            <p>
              If fore some reason you want to review your votes, just click a
              button
              below
            </p>
          </div>

          <div class="v4p__start-again-button">
            <v-btn block color="teal-accent-4" @click="currentPosition = -1">
              start again
            </v-btn>
          </div>
        </div>
      </div>
      <v-card
        class="mx-auto vote"
        height="100%"
        v-if="currentVote"
        :title="currentVote?.presentation?.title"
      >


        <v-list-item
          v-for="speaker in currentVote?.presentation?.speakers"
          :key="speaker.id"
          @click="showBio(speaker)">
          <template v-slot:title>{{ speaker.name }}</template>
          <template v-slot:prepend>
            <v-avatar color="blue-darken-2">
              <v-img
                :alt="speaker.name + ' photo'"
                :src="speaker.photo"
              ></v-img>
            </v-avatar>
          </template>
        </v-list-item>


        <v-card-text class="pt-4" v-if="showShort">
          {{ currentVote?.presentation?.shortDescription }}
        </v-card-text>
        <v-card-text class="pt-4" v-else>
          {{ currentVote?.presentation?.longDescription }}
        </v-card-text>
        <v-card-actions>
          <v-btn @click="toggleDescription" block>toggle description</v-btn>
        </v-card-actions>
        <v-card-actions>
          <v-container>
            <v-row align="center" justify="center">
              <v-col cols="auto">
                <v-btn color="red-darken-1"
                       :variant="currentVote?.rate == -1 ? 'tonal':'text' "
                       @click="vote(currentVote, -1)">-1
                </v-btn>
              </v-col>
              <v-col cols="auto">
                <v-btn color="blue-lighten"
                       :variant="currentVote?.rate == 0 ? 'tonal':'text' "
                       @click="vote(currentVote, 0)">0
                </v-btn>
              </v-col>
              <v-col cols="auto">
                <v-btn color="light-green"
                       :variant="currentVote?.rate == 1 ? 'tonal':'text' "
                       @click="vote(currentVote, 1)">+1
                </v-btn>
              </v-col>
            </v-row>
          </v-container>


        </v-card-actions>

        <v-expand-transition>
          <v-card
            v-if="showSpeaker"
            class="position-absolute w-100"
            height="100%"
            style="bottom: 0;"
            :title="showSpeaker.name "
          >
            <v-card-text class="pb-0">
              <p class="text-medium-emphasis">
                {{ showSpeaker.bio }}
              </p>
            </v-card-text>
            <template v-slot:prepend>
              <v-avatar color="blue-darken-2">
                <v-img
                  :alt="showSpeaker.name + ' photo'"
                  :src="showSpeaker.photo"
                ></v-img>
              </v-avatar>
            </template>

            <v-card-actions class="pt-0">
              <v-btn
                color="teal-accent-4"
                text="Close"
                variant="text"
                @click="showBio(null)"
              ></v-btn>
            </v-card-actions>
          </v-card>
        </v-expand-transition>
      </v-card>
    </v-container>
  </div>
</template>

<style scoped>
.content {
  width: 100%;
  height: 100%;
  background-color: #0b0a0a;
}

.speaker__bio {
  overflow: hidden;
  height: 1em;
}

.intro {
  color: white;
}

</style>
