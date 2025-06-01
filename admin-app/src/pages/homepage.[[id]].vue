<script setup lang="ts">
import {onMounted} from 'vue'
import {usersApi} from "@/utils/api.ts";
import type {
  InlinePresentation,
  InlineWorkshop,
  User
} from "@/utils/api-axios-client";
import {useRoute} from "vue-router";


function sayHi() {
  Notify.success(`Hi!`)
}

function warning() {
  Notify.warning(`How dare you refuse me.`)
}

definePage({
  meta: {
    icon: 'mdi-home',
    title: 'Homepage',
    drawerIndex: 0,
    requiresAuth: true,
  },
})


const route = useRoute()

let presentations = ref<InlinePresentation[]>([])
let workshops = ref<InlineWorkshop[]>([])
let profile = ref<User>()

let user = useAuthStore().user
const userId = route.params?.id || user!.jti

function loadPresentations() {
  usersApi.getUserPresentations(userId )
    .then(res => res.data)
    .then(data => presentations.value = data)

  usersApi.getUserWorkshops(userId)
    .then(res => res.data)
    .then(data => workshops.value = data)
}

function loadUserProfile() {
  usersApi.getById(userId)
    .then(res => res.data)
    .then(data => profile.value = data)
}

function deletePresentation(presentation: InlinePresentation) {
  usersApi.deletePresentation(userId, presentation.id!)
    .then(_ => Notify.success(`"${presentation.title}" deleted`))
    .then(_ => loadPresentations())
}

onMounted(() => {
  if (user) {
    loadPresentations();
    loadUserProfile();
  }
})
</script>

<template>

  <v-container>
    <v-row>

      <v-col cols="12" md="4">
        <v-card v-if="profile" class="mx-auto">

          <v-card-actions>
            <v-list-item class="w-100">
              <template v-slot:prepend>
                <v-avatar
                  color="grey-darken-3"
                  :image="profile.photo"
                ></v-avatar>
              </template>

              <v-list-item-title>{{ profile.name }}</v-list-item-title>

              <v-list-item-subtitle>{{ profile.email }}</v-list-item-subtitle>

              <template v-slot:append>
                <div class="justify-self-end">
                  <v-btn to="/profile-form">edit</v-btn>
                </div>
              </template>
            </v-list-item>
          </v-card-actions>
          <v-card-text class="py-2">
            {{ profile.bio }}
          </v-card-text>


          <v-list>
            <v-list-item>
              <template v-slot:prepend>
                <v-icon icon="mdi-account"></v-icon>
              </template>
              <v-list-item-title v-text="profile.username"></v-list-item-title>
            </v-list-item>
            <v-list-item>
              <template v-slot:prepend>
                <v-icon icon="mdi-alpha-x"></v-icon>
              </template>
              <v-list-item-title v-text="profile.twitter"></v-list-item-title>
            </v-list-item>
            <v-list-item>
              <template v-slot:prepend>
                <v-icon icon="mdi-github"></v-icon>
              </template>
              <v-list-item-title v-text="profile.github"></v-list-item-title>
            </v-list-item>
            <v-list-item>
              <template v-slot:prepend>
                <v-icon icon="mdi-web"></v-icon>
              </template>
              <v-list-item-title v-text="profile.www"></v-list-item-title>
            </v-list-item>
          </v-list>
        </v-card>
      </v-col>
      <v-col cols="12" md="8">

        <v-container>
          <v-row>
            <h2> Workshops </h2>

            <v-btn size="large" class="submit-new" to="/workshop-form">submit new
              workshop
            </v-btn>
          </v-row>
          <v-row v-for="workshop in workshops" :key="workshop.id">
            <v-col>
              <v-card
                :title="workshop.title"
                :subtitle="workshop.shortDescription"
                :text="workshop.description"
              >
                <v-card-actions>
                  <v-chip v-if="workshop.isFree">Free</v-chip>
                  <v-chip v-else>{{ workshop.expectedPrice }}PLN</v-chip>
                  <v-chip>{{ workshop.durationInMinutes }} min</v-chip>
                  <v-chip>{{ workshop.maxGroupSize }} max</v-chip>
                  <v-chip> {{ workshop.language }}</v-chip>
                  <v-chip> {{ workshop.level }}</v-chip>
                  <v-chip v-for="tag in workshop.tags"> {{ tag }}</v-chip>
                  <v-spacer></v-spacer>
                  <v-btn
                    color="medium-emphasis"
                    icon="mdi-delete"
                    size="small"
                  >
                    <v-icon icon="mdi-delete"></v-icon>

                    <v-menu activator="parent">
                      <v-btn @click="deletePresentation(workshop)"> YES,
                        DELETE
                      </v-btn>
                    </v-menu>
                  </v-btn>

                  <v-btn
                    color="medium-emphasis"
                    icon="mdi-pencil-outline"
                    size="small"
                    :to="`/workshop-form/${workshop.id}`"
                  ></v-btn>
                </v-card-actions>
              </v-card>
            </v-col>
          </v-row>
          <v-row>
            <h2> Presentations </h2>

            <v-btn size="large" class="submit-new" to="/presentation-form">submit
              new presentation
            </v-btn>
          </v-row>
          <v-row v-for="presentation in presentations" :key="presentation.id">
            <v-col>

              <v-card
                :title="presentation.title"
                :subtitle="presentation.shortDescription"
                :text="presentation.description"
              >
                <v-card-actions>
                  <v-chip> {{ presentation.language }}</v-chip>
                  <v-chip> {{ presentation.level }}</v-chip>
                  <v-chip v-for="tag in presentation.tags"> {{ tag }}</v-chip>
                  <v-spacer></v-spacer>
                  <v-btn
                    color="medium-emphasis"
                    icon="mdi-delete"
                    size="small"
                  >
                    <v-icon icon="mdi-delete"></v-icon>

                    <v-menu activator="parent">
                      <v-btn @click="deletePresentation(presentation)"> YES,
                        DELETE
                      </v-btn>
                    </v-menu>
                  </v-btn>

                  <v-btn
                    color="medium-emphasis"
                    icon="mdi-pencil-outline"
                    size="small"
                    :to="`/presentation-form/${presentation.id}`"
                  ></v-btn>
                </v-card-actions>
              </v-card>
            </v-col>
          </v-row>
        </v-container>
      </v-col>
    </v-row>
  </v-container>


</template>

<style scoped>
.wrapper {
  position: relative;
  top: calc(50vh - 330px);
  text-align: center;
}

.submit-new {
  width: 100%;
}
</style>
