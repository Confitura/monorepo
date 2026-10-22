<script setup lang="ts">
import DialogConfirm from '@/components/DialogConfirm.vue'
import MergePreviewDetails from '@/components/MergePreviewDetails.vue'
import type {DataTableHeaders} from '@/plugins/vuetify'
import {adminMergeUsers, createManual, markAsAdmin, markAsVolunteer, getAllUsers, previewAdminMerge, unlinkLogin} from "@/utils/api.ts";
import type {FullUser, LoginDto, MergePreview} from "@/utils/api";
import {providerLabel} from "@/utils/sign-in-attempt.ts";


definePage({
  meta: {
    title: 'Users',
    icon: 'mdi-animation',
  },
})

const search = ref('')
const confirmDialog = useTemplateRef('confirmDialog')
const addDialog = ref(false)
const newUser = reactive({
  name: '',
  email: '',
  bio: '',
  admin: false,
  volunteer: false,
})

async function createUser() {
  if (!newUser.name || !newUser.email) {
    // @ts-ignore
    Notify?.error?.('Name and email are required')
    return
  }
  try {
    await createManual({ body: newUser })
    // @ts-ignore
    Notify?.success?.('User created')
    addDialog.value = false
    // reset
    newUser.name = ''
    newUser.email = ''
    newUser.bio = ''
    newUser.admin = false
    newUser.volunteer = false
    reloadUsers()
  } catch (e) {
    console.error(e)
    // @ts-ignore
    Notify?.error?.('Failed to create user')
  }
}

function dialogFlipAdmin(user: FullUser) {
  const message = user.isAdmin
      ? `Are you sure you want to remove admin rights from ${user.name}?`
      : `Are you sure you want to add admin to ${user.name}?`;

  confirmDialog.value
      ?.open(message)
      .then(async (confirmed: boolean) => {
        if (confirmed) {
          markAsAdmin({ path: { userId: user.id!, isAdmin: !user.isAdmin } })
              .then(_ => reloadUsers())
              .catch(_ => Notify.error('Failed'))
        }
      })
}

function dialogFlipVolunteer(user: FullUser) {
  const message = user.isVolunteer
      ? `Are you sure you want to remove volunteer rights from ${user.name}?`
      : `Are you sure you want to add volunteer to ${user.name}?`;

  confirmDialog.value
      ?.open(message)
      .then(async (confirmed: boolean) => {
        if (confirmed) {
          markAsVolunteer({ path: { userId: user.id!, isVolunteer: !user.isVolunteer } })
              .then(_ => reloadUsers())
              .catch(_ => Notify.error('Failed'))
        }
      })
}

// Unlike the User themselves, an admin may remove the last Login, which leaves the User like a Manual User.
function dialogUnlinkLogin(user: FullUser, login: LoginDto) {
  const provider = providerLabel(login.provider)
  const message = user.logins?.length === 1
      ? `Remove ${user.name}'s ${provider} login? It is their last one: they will not be able to sign in.`
      : `Remove ${user.name}'s ${provider} login?`;

  confirmDialog.value
      ?.open(message)
      .then(async (confirmed: boolean) => {
        if (confirmed) {
          unlinkLogin({ path: { id: user.id!, loginId: login.id! }, throwOnError: true })
              .then(_ => reloadUsers())
              .catch(_ => Notify.error('Failed to remove the login'))
        }
      })
}

// Merge (ADR-0003): the admin picks the other User and which of the two to keep; the other is deleted. The only way
// to join a Manual User, which has no Login to sign in with, to that person's other User.
const mergeDialog = ref(false)
const mergeUser = ref<FullUser | null>(null)
const mergeOtherId = ref<string | null>(null)
const mergeSurvivorId = ref<string | null>(null)
const mergeCandidates = computed(() => users.value.filter((user) => user.id !== mergeUser.value?.id))
const mergeOther = computed(() => users.value.find((user) => user.id === mergeOtherId.value))
// Two Users of one person often share a name and email, so the choice of which to keep also shows their Logins.
const userLabel = (user: FullUser) => user.email ? `${user.name} (${user.email})` : user.name ?? ''
const loginsLabel = (user: FullUser) => user.logins?.length ? user.logins.map((login) => login.provider).join(', ') : 'Manual'

function openMerge(user: FullUser) {
  mergeUser.value = user
  mergeOtherId.value = null
  mergeSurvivorId.value = null
  mergeDialog.value = true
}

// Both Users, once the other User and the one to keep are chosen.
const mergePair = computed(() => {
  if (!mergeOtherId.value || !mergeSurvivorId.value) return null
  const survivorId = mergeSurvivorId.value
  const absorbedId = survivorId === mergeUser.value!.id ? mergeOtherId.value : mergeUser.value!.id!
  return { survivorId, absorbedId }
})
// What the merge would make of the kept User, or why the backend would refuse it.
const mergePreview = ref<MergePreview | null>(null)
const mergeRefusal = ref<string | null>(null)

// A 409 says why, e.g. both users hold a ticket.
function refusalMessage(error: unknown, fallback: string) {
  const { status, response } = error as { status?: number, response?: { data?: unknown } }
  return status === 409 && typeof response?.data === 'string' ? response.data : fallback
}

// The backend refuses the preview exactly when it would refuse the merge. A newer choice discards an older answer.
watch(mergePair, async (pair, _, onCleanup) => {
  let stale = false
  onCleanup(() => stale = true)
  mergePreview.value = null
  mergeRefusal.value = null
  if (!pair) return
  try {
    const response = await previewAdminMerge({ path: pair, throwOnError: true })
    if (!stale) mergePreview.value = response.data
  } catch (error) {
    if (!stale) mergeRefusal.value = refusalMessage(error, 'Failed to preview the merge')
  }
})

function confirmMerge() {
  adminMergeUsers({ path: mergePair.value!, throwOnError: true })
      .then(_ => {
        Notify.success('Users merged')
        mergeDialog.value = false
        reloadUsers()
      })
      .catch(error => Notify.error(refusalMessage(error, 'Failed to merge the users')))
}

const headers: DataTableHeaders = [
  {title: 'Name', key: 'name'},
  {title: 'Email', key: 'email'},
  {
    title: 'Logins',
    key: 'logins',
    value: loginsLabel,
  },
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
  getAllUsers()
      .then(res => res.data ?? [])
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
            <div class="d-flex align-center">
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
              <v-btn color="primary" class="ml-2" prepend-icon="mdi-account-plus" @click="addDialog = true">Add User</v-btn>
            </div>
          </teleport>
          <v-data-table
              ref="table"
              :headers="headers"
              :items="users"
              item-value="name"
              :filter-keys="['name','email']"
              :search="search"
          >
            <template #item.logins="{ item }">
              <v-btn
                  v-for="login in item.logins"
                  :key="login.id"
                  size="x-small"
                  variant="tonal"
                  class="mr-1"
                  append-icon="mdi-close"
                  :aria-label="`Remove ${providerLabel(login.provider)} login`"
                  @click.stop="dialogUnlinkLogin(item, login)"
              >{{ providerLabel(login.provider) }}</v-btn>
              <template v-if="!item.logins?.length">Manual</template>
            </template>
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
                        icon="mdi-account-edit"
                        v-bind="props"
                        :href="`/profile-form/${item.id}`"
                        target="_blank"
                        rel="noopener"
                    />
                  </template>
                  <span>Edit user</span>
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
                <v-tooltip location="top">
                  <template #activator="{ props }">
                    <v-btn
                        icon="mdi-call-merge"
                        v-bind="props"
                        :aria-label="`Merge ${item.name}`"
                        @click.stop="openMerge(item)"
                    />
                  </template>
                  <span>Merge with another user</span>
                </v-tooltip>


              </v-defaults-provider>
            </template>
          </v-data-table>
          <DialogConfirm ref="confirmDialog"/>
        </v-card>
      </v-col>
    </v-row>

    <v-dialog v-model="addDialog" max-width="600">
      <v-card>
        <v-card-title>Add User</v-card-title>
        <v-card-text>
          <v-text-field v-model="newUser.name" label="Name" required />
          <v-text-field v-model="newUser.email" label="Email" type="email" required />
          <v-textarea v-model="newUser.bio" label="Bio" rows="3" />
          <div class="d-flex gap-4 mt-2">
            <v-checkbox v-model="newUser.admin" label="Admin" hide-details />
            <v-checkbox v-model="newUser.volunteer" label="Volunteer" hide-details />
          </div>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="addDialog = false">Cancel</v-btn>
          <v-btn color="primary" @click="createUser">Create</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="mergeDialog" max-width="600">
      <v-card v-if="mergeUser">
        <v-card-title>Merge {{ mergeUser.name }} with another user</v-card-title>
        <v-card-text>
          <v-autocomplete
              v-model="mergeOtherId"
              :items="mergeCandidates"
              :item-title="userLabel"
              item-value="id"
              label="Other user (search by name or email)"
              @update:model-value="mergeSurvivorId = null"
          />
          <v-radio-group v-if="mergeOther" v-model="mergeSurvivorId" label="Keep">
            <v-radio
                v-for="user in [mergeUser, mergeOther]"
                :key="user.id"
                :label="`${userLabel(user)}, logins: ${loginsLabel(user)}`"
                :value="user.id"
            />
          </v-radio-group>
          <template v-if="mergePreview">
            <p>The kept user will be:</p>
            <MergePreviewDetails
                :preview="mergePreview"
                roles-lead="The kept user also becomes"
                talks-heading="These talks move to the kept user:"
            />
          </template>
          <v-alert v-if="mergeRefusal" type="error" variant="tonal" class="mb-2" :text="mergeRefusal" />
          <v-alert
              type="warning"
              variant="tonal"
              text="The other user is deleted: its logins, talks and ticket move to the one kept. This cannot be undone."
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="mergeDialog = false">Cancel</v-btn>
          <v-btn v-if="!mergeRefusal" color="primary" :disabled="!mergePreview" @click="confirmMerge">Merge</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>
