<script setup lang="ts">
import DialogConfirm from '@/components/DialogConfirm.vue'
import type {DataTableHeaders} from '@/plugins/vuetify'
import {adminUsersApi} from "@/utils/api.ts";
import type {FullUser} from "@/utils/api-axios-client";


definePage({
  meta: {
    title: 'Users',
    icon: 'mdi-animation',
  },
})

const search = ref('')
const confirmDialog = useTemplateRef('confirmDialog')

function dialogFlipAdmin(user: FullUser) {
  let message = user.isAdmin
      ? `Are you sure you want to remove admin rights from ${user.name}?`
      : `Are you sure you want to add admin to ${user.name}?`;

  confirmDialog.value
      ?.open(message)
      .then(async (confirmed: boolean) => {
        if (confirmed) {
          adminUsersApi.markAsAdmin(user.id!, !user.isAdmin)
              .then(_ => reloadUsers())
              .catch(_ => Notify.error('Failed'))
        }
      })
}

function dialogFlipVolunteer(user: FullUser) {
  let message = user.isVolunteer
      ? `Are you sure you want to remove volunteer rights from ${user.name}?`
      : `Are you sure you want to add volunteer to ${user.name}?`;

  confirmDialog.value
      ?.open(message)
      .then(async (confirmed: boolean) => {
        if (confirmed) {
          adminUsersApi.markAsVolunteer(user.id!, !user.isVolunteer)
              .then(_ => reloadUsers())
              .catch(_ => Notify.error('Failed'))
        }
      })
}


const headers: DataTableHeaders = [
  {title: 'Name', key: 'name'},
  {title: 'Email', key: 'email'},
  {title: 'Origin', key: 'origin'},
  {title: 'Admin', key: 'isAdmin'},
  {title: 'Volunteer', key: 'isVolunteer'},
  {title: 'Speaker', key: 'isSpeaker'},
  {title: 'Accepted Policy', key: 'privacyPolicyAccepted'},
  {title: 'Participant', key: 'isParticipant'},
  {title: 'Has accepted presentation', key: 'hasAcceptedPresentation'},
  {title: 'Actions', key: 'actions', sortable: false},
]
const users = ref<FullUser[]>([])


function reloadUsers() {
  adminUsersApi.getAllUsers()
      .then(res => res.data)
      .then((data: Array<FullUser>) => users.value = data)
      .catch(e => console.error(e))
}

onMounted(() => {
  reloadUsers();
});
</script>

<template>
  <v-container fluid>
    <v-row>
      <v-col>
        <v-card>
          <teleport to="#app-bar">
            <v-text-field
                v-model="search"
                prepend-inner-icon="mdi-magnify"
                label="Search"
                single-line
                hide-details
                density="compact"
                class="mr-2"
                rounded="xl"
                flat
                variant="solo"
                style="width: 250px"
            />
          </teleport>
          <v-data-table
              ref="table"
              :headers="headers"
              :items="users"
              item-value="name"
              :filter-keys="['name','email']"
              :search="search"
          >
            <template #item.actions="{ item }">
              <v-defaults-provider
                  :defaults="{
                  VBtn: {
                    size: 20,
                    rounded: 'sm',
                    variant: 'text',
                    class: 'ml-1',
                    color: '',
                  },
                  VIcon: {
                    size: 20,
                  },
                }"
              >
                <v-tooltip location="top">
                  <template #activator="{ props }">
                    <v-btn
                        icon="mdi-account-heart"
                        v-bind="props"
                        @click.stop="dialogFlipVolunteer(item)"
                    />
                  </template>
                  <span>Flip Volunteer</span>
                </v-tooltip>
                <v-tooltip location="top">
                  <template #activator="{ props }">
                    <v-btn
                        icon="mdi-account-star"
                        v-bind="props"
                        @click.stop="dialogFlipAdmin(item)"
                    />
                  </template>
                  <span>Flip Admin</span>
                </v-tooltip>
                <v-tooltip location="top">
                  <template #activator="{ props }">
                    <v-btn
                        icon="mdi-magnify-expand"
                        v-bind="props"
                        :to="`/homepage/${item.id}`"
                    />
                  </template>
                  <span>show user</span>
                </v-tooltip>


              </v-defaults-provider>
            </template>
          </v-data-table>
          <DialogConfirm ref="confirmDialog"/>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>
