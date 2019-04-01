<template>
    <div class="profile">
        <Box class="content" color="white">
            <div v-if="profile" class="registration-form">
                <div class="picture">
                    <div>
                        <label>Pick photo <br/>
                            <input type="file" id="file" ref="file" v-on:change="handleFileUpload()"/>
                        </label>
                        <div v-if="errors.photo" class="errors">
                            <span v-for="error in errors.photo">
                                {{error}}
                            </span>
                        </div>
                    </div>
                </div>
                <div class="fields"
                     @submit="save"
                >
                    <form>
                        <div>
                            <label>Full Name<br/>
                                <input placeholder="name" v-model="profile.name" class="input">
                            </label>
                            <div v-if="errors.name" class="errors">
                            <span v-for="error in errors.name">
                                {{error}}
                            </span>
                            </div>
                        </div>
                        <div>
                            <label>E-Mail<br/>
                                <input placeholder="email" v-model="profile.email" type="email" class="input">
                            </label>
                            <div v-if="errors.email" class="errors">
                            <span v-for="error in errors.email">
                                {{error}}
                            </span>
                            </div>
                        </div>
                        <div>
                            <label>Bio<br/>
                                <textarea placeholder="bio" v-model="profile.bio" class="input"
                                          rows="20"></textarea>
                            </label>
                            <div v-if="errors.bio" class="errors">
                            <span v-for="error in errors.bio">
                                {{error}}
                            </span>
                            </div>
                        </div>

                        <div>
                            <input type="checkbox" v-model="profile.privacyPolicyAccepted" id="privacyPolicyAccepted">

                            <label for="privacyPolicyAccepted">I accept the privacy
                                policy
                            </label>

                            <div v-if="errors.privacyPolicyAccepted" class="errors">
                            <span v-for="error in errors.privacyPolicyAccepted">
                                {{error}}
                            </span>
                            </div>
                        </div>

                        <div v-if="errors.form" class="errors">
                            <span v-for="error in errors.form">
                                {{error}}
                            </span>
                        </div>
                        <button type="submit">save</button>
                    </form>
                </div>
            </div>
        </Box>
        <TheContact id="contact"/>
    </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { LOAD_CURRENT_PROFILE } from '@/store.user-profile';
import Box from '@/components/Box.vue';
import TheContact from '@/components/TheContact.vue';
import { UserProfile } from '@/types';

import axios from 'axios';

@Component({
  components: { Box, TheContact },
})
export default class RegisterPage extends Vue {
  public $refs!: Vue['$refs'] & {
    file: {
      files: File[],
    },
  };
  public profile: UserProfile | null = null;
  public photo: File | null = null;
  public errors: RegisterErrors = {};
  // tslint:disable
  private emailPattern = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

  // tslint:enable

  public mounted() {

    this.$store.dispatch(LOAD_CURRENT_PROFILE)
      .then(() => {
        this.profile = this.$store.state.userProfile.currentProfile;
      });
  }

  public save(event: Event) {
    event.preventDefault();
    if (this.validate()) {
      axios
        .post<any>('/api/users', this.profile, { headers: { Authorization: `Bearer ${this.$store.state.token}` } })
        .then((it: any) => {
          this.uploadPhoto();
          return it;
        })
        .then((it: any) => {
          this.$router.push('/profile');
          return it;
        })
        .catch((error: any) => this.uploadFailed(error));
    }
  }

  public handleFileUpload() {
    const { files } = this.$refs.file;
    this.photo = files[0];
  }

  public validEmail(email: string) {
    return this.emailPattern.test(email);
  }

  private uploadPhoto() {
    if (this.photo !== null && this.profile !== null) {
      const formData = new FormData();
      formData.append('file', this.photo);
      const headers = { Authorization: `Bearer ${this.$store.state.token}` };
      return axios
        .post(`/api/resources/${this.profile.id}`, formData, { headers });
    }
  }

  private validate() {
    this.errors = {};
    let valid = true;
    if (this.profile === null) {
      return;
    }
    if (!this.validEmail(this.profile.email)) {
      this.errors.email = ['invalid email'];
      valid = false;
    }
    if (!this.photo) {
      this.errors.photo = ['Photo is required'];
      valid = false;
    }
    if (!this.profile.name) {
      this.errors.name = ['Name is required'];
      valid = false;
    }
    if (!this.profile.bio || this.profile.bio.length < 100) {
      this.errors.bio = ['Bio should be at least 100 characters long'];
      valid = false;
    }
    if (!this.profile.privacyPolicyAccepted) {
      this.errors.privacyPolicyAccepted = ['Agreeing to our policy is required'];
      valid = false;
    }
    return valid;
  }

  private uploadFailed(error: any) {
    this.errors = {
      form: [
        'Submit failed',
        error.response.data.message,
      ],
    };
  }
}

interface RegisterErrors {
  bio?: string[];
  email?: string[];
  photo?: string[];
  name?: string[];
  privacyPolicyAccepted?: string[];
  form?: string[];
}
</script>

<style lang="scss" scoped>
    .picture {
        word-wrap: break-word;
        grid-area: picture;
    }

    .fields {
        grid-area: fields;

        .input {
            width: 100%;
        }

    }

    .registration-form {
        display: grid;
        width: 100vw;
        justify-content: stretch;
        padding-left: 20%;
        padding-right: 20%;
        max-width: 800px;
        grid-template-columns: 1fr 2fr;
        grid-template-rows: auto;
        grid-template-areas: "picture fields";
        margin-top: 30vh;
    }

    .errors {
        color: red;
    }
</style>
