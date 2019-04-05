<template>
    <div class="profile">
        <Box class="content " color="white" :full="false">

            <div class="back-office" v-if="profile && activeUser">
                <div class="row">
                    <form class="col s12" @submit="save" novalidate>
                        <div class="row">
                            <div class="input-field col s12">
                                <input id="full_name" type="text" class=""
                                       v-model="profile.name" required>
                                <label for="full_name">Full name</label>
                                <span class="errors" v-for="error in errors.name">{{error}}</span>
                            </div>
                        </div>
                        <div class="row">
                            <div class="input-field col s12">
                                <input id="email" type="email" class=""
                                       v-model="profile.email" required>
                                <label for="email">E-mail</label>
                                <span class="errors" v-for="error in errors.email">{{error}}</span>
                            </div>
                        </div>
                        <div class="row">
                            <div class="input-field col s12">
                                <textarea id="Bio" type="text" class="materialize-textarea"
                                          ref="bio"
                                          v-model="profile.bio">
                                </textarea>
                                <label for="Bio">Bio</label>
                                <span class="errors" v-for="error in errors.bio">{{error}}</span>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col s12">
                                <label>
                                    <input type="checkbox" v-model="profile.privacyPolicyAccepted"
                                           id="privacyPolicyAccepted" required>
                                    <span>I accept the privacy policy</span>
                                </label>
                                <div class="errors" v-for="error in errors.privacyPolicyAccepted">{{error}}</div>

                            </div>
                        </div>
                        <span v-for="error in errors.form">
                             {{error}} <br/>
                        </span>

                        <div>
                            <button class="btn waves-effect waves-light button button--save" type="submit" name="action">Save
                            </button>
                            <button v-if="activeUser.isNew" class="btn waves-effect waves-light button button--cancel" type="button" name="action">Cancel
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </Box>
        <TheContact id="contact"/>
    </div>

</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import { LOAD_CURRENT_PROFILE } from '@/store/store.user-profile';
  import Box from '@/components/Box.vue';
  import TheContact from '@/components/TheContact.vue';
  import { User, UserProfile } from '@/types';
  import M from 'materialize-css';
  import axios from 'axios';

  @Component({
    components: { Box, TheContact },
  })
  export default class RegisterPage extends Vue {
    public $refs!: Vue['$refs'] & {
      file: {
        files: File[],
      },
      bio: Element,
    };
    public profile: UserProfile | null = {};
    public photo: File | null = null;
    public errors: RegisterErrors = {};
    public activeUser!: User;
    // tslint:disable
    private emailPattern = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

    // tslint:enable

    public mounted() {
      M.AutoInit();
      this.activeUser = this.$store.getters.user;
      this.$store.dispatch(LOAD_CURRENT_PROFILE)
        .then(() => {
          this.profile = this.$store.state.userProfile.currentProfile;
          M.textareaAutoResize(this.$refs.bio);
        });
    }

    public updated() {
      M.updateTextFields();
    }

    public save(event: Event) {
      event.preventDefault();
      if (this.validate()) {
        axios
          .post<any>('/api/users', this.profile)
          .then(() => this.$router.push('/profile'))
          .catch((error: any) => this.uploadFailed(error));
      }
    }

    public validEmail(email: string) {
      return this.emailPattern.test(email);
    }

    private validate() {
      this.errors = {};
      let valid = true;
      if (this.profile === null) {
        return;
      }
      if (this.profile.email && !this.validEmail(this.profile.email)) {
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

        &, &:focus, &:hover {
            background-color: $brand;
        }
    }

    .button--cancel {
        &, &:focus, &:hover {
            background-color: $brand;
        }
    }
</style>
