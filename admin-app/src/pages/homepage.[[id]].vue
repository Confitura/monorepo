<script setup lang="ts">
import {onMounted} from 'vue'
import {usersApi, presentationApi} from "@/utils/api.ts";
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
let cospeakers = ref<User[]>([])
let newCospeakerEmail = ref('')
let selectedPresentation = ref<InlinePresentation | null>(null)
let selectedWorkshop = ref<InlineWorkshop | null>(null)
let cospeakersDialogVisible = ref(false)
let isWorkshop = ref(false)

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

function loadCospeakers(id: string, workshop: boolean = false) {
  isWorkshop.value = workshop

  if (workshop) {
    selectedWorkshop.value = workshops.value.find(w => w.id === id) || null
    selectedPresentation.value = null
  } else {
    selectedPresentation.value = presentations.value.find(p => p.id === id) || null
    selectedWorkshop.value = null
  }

  presentationApi.getCospeakers(id)
    .then(res => res.data)
    .then(data => {
      cospeakers.value = data
      cospeakersDialogVisible.value = true
    })
    .catch(error => {
      Notify.error(error.response?.data || "Failed to load cospeakers")
    })
}

function addCospeaker(id: string) {
  if (!newCospeakerEmail.value) {
    Notify.warning("Please enter an email address")
    return
  }

  presentationApi.addCospeaker(id, newCospeakerEmail.value)
    .then(res => {
      Notify.success(`Cospeaker added`)
      newCospeakerEmail.value = ''
      loadCospeakers(id, isWorkshop.value)
    })
    .catch(error => {
      Notify.error(error.response?.data || "Failed to add cospeaker")
    })
}

function removeCospeaker(id: string, cospeakerId: string) {
  presentationApi.removeCospeaker(id, cospeakerId)
    .then(_ => {
      Notify.success(`Cospeaker removed`)
      loadCospeakers(id, isWorkshop.value)
    })
    .catch(error => {
      Notify.error(error.response?.data || "Failed to remove cospeaker")
    })
}

function deleteItem(item: InlinePresentation | InlineWorkshop, isWorkshop: boolean = false) {
  if (isWorkshop) {
    usersApi.deleteWorkshop(userId, item.id!)
      .then(_ => Notify.success(`"${item.title}" deleted`))
      .then(_ => loadPresentations())
      .catch(error => {
        Notify.error(error.response?.data || "Failed to delete workshop")
      })
  } else {
    usersApi.deletePresentation(userId, item.id!)
      .then(_ => Notify.success(`"${item.title}" deleted`))
      .then(_ => loadPresentations())
      .catch(error => {
        Notify.error(error.response?.data || "Failed to delete presentation")
      })
  }
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

            <v-btn size="large" class="submit-new" to="/workshop-form" v-if="user?.isAdmin">submit new
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
                    v-if="user?.isAdmin"
                  >
                    <v-icon icon="mdi-delete"></v-icon>

                    <v-menu activator="parent">
                      <v-btn @click="deleteItem(workshop, true)"> YES,
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

                  <v-btn
                    color="medium-emphasis"
                    icon="mdi-account-multiple"
                    size="small"
                    @click="loadCospeakers(workshop.id!, true)"
                  ></v-btn>
                </v-card-actions>
              </v-card>
            </v-col>
          </v-row>
          <v-row>
            <h2> Presentations </h2>

            <v-btn size="large" class="submit-new" to="/presentation-form" v-if="user?.isAdmin">submit
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
                  <v-btn v-if="user?.isAdmin"
                    color="medium-emphasis"
                    icon="mdi-delete"
                    size="small"
                  >
                    <v-icon icon="mdi-delete"></v-icon>

                    <v-menu activator="parent" >
                      <v-btn @click="deleteItem(presentation)"> YES,
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

                  <v-btn
                    color="medium-emphasis"
                    icon="mdi-account-multiple"
                    size="small"
                    @click="loadCospeakers(presentation.id!)"
                  ></v-btn>
                </v-card-actions>
              </v-card>
            </v-col>
          </v-row>
        </v-container>
      </v-col>
    </v-row>
  </v-container>

  <!-- Cospeakers Dialog -->
  <v-dialog v-model="cospeakersDialogVisible" max-width="600px">
    <v-card v-if="selectedPresentation || selectedWorkshop">
      <v-card-title>
        Manage Speakers for "{{ isWorkshop ? selectedWorkshop?.title : selectedPresentation?.title }}"
      </v-card-title>

      <v-card-text>
        <v-list>
          <v-list-subheader>Current Speakers</v-list-subheader>
          <v-list-item v-for="cospeaker in cospeakers" :key="cospeaker.id">
            <v-list-item-title>{{ cospeaker.name }} ({{ cospeaker.email }})</v-list-item-title>
            <template v-slot:append>
              <v-btn
                color="error"
                icon="mdi-delete"
                size="small"
                @click="removeCospeaker(isWorkshop ? selectedWorkshop!.id! : selectedPresentation!.id!, cospeaker.id)"
              ></v-btn>
            </template>
          </v-list-item>

          <v-divider class="my-4"></v-divider>

          <v-list-subheader>Add New Speaker</v-list-subheader>
          <v-form @submit.prevent="addCospeaker(isWorkshop ? selectedWorkshop!.id! : selectedPresentation!.id!)">
            <v-text-field
              v-model="newCospeakerEmail"
              label="Cospeaker Email"
              placeholder="Enter email address"
              required
            ></v-text-field>
            <v-btn
              color="primary"
              type="submit"
              :disabled="!newCospeakerEmail"
            >
              Add Cospeaker
            </v-btn>
          </v-form>
        </v-list>
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="primary" @click="cospeakersDialogVisible = false">Close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

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
