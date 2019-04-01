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
  import {Component, Vue} from 'vue-property-decorator';
  import {LOAD_CURRENT_PROFILE} from "@/store.user-profile";
  import Box from "@/components/Box.vue";
  import TheContact from "@/components/TheContact.vue";
  import {UserProfile} from "@/types";

  import axios from 'axios';

  @Component({
    components: {Box, TheContact},
  })
  export default class RegisterPage extends Vue {
    profile: UserProfile | null = null;
    photo: File | null = null;
    errors = {};

    mounted() {

      this.$store.dispatch(LOAD_CURRENT_PROFILE)
        .then(() => {
          this.profile = this.$store.state.userProfile.currentProfile
        });
    }

    save(e) {
      e.preventDefault();
      if (this.validate()) {
        axios
          .post("/api/users", this.profile, {headers: {Authorization: `Bearer ${this.$store.state.token}`}})
          .then(it => this.uploadPhoto())
          .then(it => this.$router.push('/profile'))
          .catch((error) => this.uploadFailed(error))
      }
    }

    handleFileUpload() {
      this.photo = this.$refs.file['files'][0];
    }

    private uploadPhoto() {
      let formData = new FormData();
      formData.append('file', this.photo);
      return axios.post(`/api/resources/${this.profile.id}`, formData, {headers: {Authorization: `Bearer ${this.$store.state.token}`}})

    }

    private validate() {
      this.errors = {};
      var valid = true;
      if (!this.validEmail(this.profile.email)) {
        this.errors['email'] = ['invalid email'];
        valid = false;
      }
      if (!this.photo) {
        this.errors['photo'] = ['Photo is required'];
        valid = false;
      }
      if (!this.profile.name) {
        this.errors['name'] = ['Name is required'];
        valid = false;
      }
      if (!this.profile.bio || this.profile.bio.length < 100) {
        this.errors['bio'] = ['Bio should be at least 100 characters long'];
        valid = false;
      }
      if (!this.profile.privacyPolicyAccepted) {
        this.errors['privacyPolicyAccepted'] = ['Agreeing to our policy is required'];
        valid = false;
      }
      return valid;
    }

    validEmail(email) {
      var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
      return re.test(email);
    }

    private uploadFailed(error: any) {
      console.log('uploadFailed', error.response);
      this.errors = {
        form: [
          "Submit failed",
          error.response.data.message
        ]
      }
    }
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
