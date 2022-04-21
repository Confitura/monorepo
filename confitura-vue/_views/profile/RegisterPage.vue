<template>
  <div class="profile">
    <PageHeader title="My Profile" :small="true"></PageHeader>
    <Box class="content " color="white" :full="false">
      <div class="back-office" v-if="profile && activeUser">
        <div class="row">
          <form class="col s12" @submit="save" novalidate>
            <div class="row">
              <div class="input-field col s12">
                <input
                  id="full_name"
                  type="text"
                  class=""
                  v-model="profile.name"
                  required
                />
                <label for="full_name">Full name</label>
                <span class="errors" v-for="error in errors.name" :key="error">
                  {{ error }}
                </span>
              </div>
            </div>
            <div class="row">
              <div class="input-field col s12">
                <input
                  id="email"
                  type="email"
                  v-model="profile.email"
                  required
                />
                <label for="email">E-mail</label>
                <span class="errors" v-for="error in errors.email" :key="error">
                  {{ error }}
                </span>
              </div>
            </div>
            <div class="row">
              <div class="input-field col s12">
                <textarea
                  id="Bio"
                  type="text"
                  class="materialize-textarea"
                  ref="bio"
                  v-model="profile.bio"
                >
                </textarea>
                <label for="Bio">Bio</label>

                <span class="errors" v-for="error in errors.bio" :key="error">{{
                  error
                }}</span>
              </div>
            </div>
            <div class="row">
              <div class="input-field col s12">
                <input id="twitter" type="text" v-model="profile.twitter" />
                <label for="twitter">twitter</label>
              </div>
            </div>
            <div class="row">
              <div class="input-field col s12">
                <input id="github" type="text" v-model="profile.github" />
                <label for="github">github</label>
              </div>
            </div>
            <div class="row">
              <div class="input-field col s12">
                <input id="www" type="text" v-model="profile.www" />
                <label for="www">www</label>
              </div>
            </div>

            <div class="row">
              <div class="col s12">
                <label>
                  <input
                    type="checkbox"
                    v-model="profile.privacyPolicyAccepted"
                    id="privacyPolicyAccepted"
                    required
                  />
                  <span
                    >I accept the
                    <router-link to="privacy-policy"
                      >privacy policy</router-link
                    ></span
                  >
                </label>
                <div
                  class="errors"
                  v-for="error in errors.privacyPolicyAccepted"
                  :key="error"
                >
                  {{ error }}
                </div>
              </div>
            </div>
            <span v-for="error in errors.form" :key="error">
              {{ error }} <br />
            </span>

            <div>
              <button
                class="btn waves-effect waves-light button button--save"
                type="submit"
                name="action"
              >
                Save
              </button>
              <button
                v-if="activeUser.isNew"
                class="btn waves-effect waves-light button button--cancel"
                type="button"
                name="action"
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </Box>
    <Contact />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { LOAD_PROFILE_BY_ID } from "@/store/store.user-profile";
import Box from "@/components/Box.vue";
import Contact from "@/components/Contact.vue";
import { Participant, PARTICIPATION_ID, User, UserProfile } from "@/types";
import M from "materialize-css";
import axios from "axios";
import PageHeader from "@/components/PageHeader.vue";
import { validEmail } from "@/validation-utils";

@Component({
  components: { PageHeader, Box, Contact }
})
export default class RegisterPage extends Vue {
  public $refs!: Vue["$refs"] & {
    file: {
      files: File[];
    };
    bio: Element;
  };
  public profile: UserProfile | null = {};
  public errors: RegisterErrors = {};
  public activeUser: User | null = null;

  public mounted() {
    M.AutoInit();
    this.activeUser = this.$store.getters.user;
    this.loadProfile().then(() => {
      this.profile = this.$store.state.userProfile.currentProfile;
      setTimeout(() => M.textareaAutoResize(this.$refs.bio));
      if (localStorage.getItem(PARTICIPATION_ID)) {
        this.suggestDataFromParticipant();
      }
    });
  }

  public updated() {
    M.updateTextFields();
  }

  public save(event: Event) {
    event.preventDefault();
    if (this.validate()) {
      axios
        .post<any>("/api/users", this.profile)
        .then(it => {
          if (this.profile && this.profile.id) {
            this.$router.push("/profile/" + this.profile.id);
          } else {
            this.$router.push("/profile");
          }
          return it;
        })
        .catch((error: any) => this.uploadFailed(error));
    }
  }

  private suggestDataFromParticipant() {
    const participationId = localStorage.getItem(PARTICIPATION_ID);
    axios.get<Participant>(`/api/participants/${participationId}`).then(it => {
      const name = (it.data.firstName || "") + " " + (it.data.lastName || "");
      if (this.profile) {
        this.profile.name = name;
        this.profile.email = it.data.email || "";
        this.profile.participationDataId = participationId;
      }
    });
  }

  private loadProfile() {
    const userId = this.$route.params.id || this.$store.getters.user.jti;
    return this.$store.dispatch(LOAD_PROFILE_BY_ID, { id: userId });
  }

  private validate() {
    const errors: RegisterErrors = {};
    let valid = true;
    if (this.profile === null) {
      return;
    }
    if (!this.profile.email || !validEmail(this.profile.email)) {
      errors.email = ["invalid email"];
      valid = false;
    }
    if (!this.profile.name) {
      errors.name = ["Name is required"];
      valid = false;
    }
    if (!this.profile.privacyPolicyAccepted) {
      errors.privacyPolicyAccepted = ["Agreeing to our policy is required"];
      valid = false;
    }
    this.errors = errors;
    return valid;
  }

  private uploadFailed(error: any) {
    this.errors = {
      form: ["Submit failed", error.response.data.message]
    };
  }
}

interface RegisterErrors {
  bio?: string[];
  email?: string[];
  name?: string[];
  privacyPolicyAccepted?: string[];
  form?: string[];
}
</script>

<style lang="scss" scoped>
@import "../../assets/colors";

.back-office {
  padding-top: 10vh;
}

.errors {
  color: red;
}

.button {
  width: 100px;
}

.button--save {
  margin-right: 1rem;

  &,
  &:focus,
  &:hover {
    background-color: $brand;
  }
}

.button--cancel {
  &,
  &:focus,
  &:hover {
    background-color: $brand;
  }
}
</style>
