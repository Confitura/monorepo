<template>
    <div class="profile">
        <PageHeader></PageHeader>
        <Box class="content" color="white">
            <div class="back-office" v-if="profile">
                <div class="row">

                    <div class="col s12 m6 l4">
                        <div class="card">
                            <div class="card-image" :key="photoKey">
                                <div class="photo-container">
                                    <img :src="profile.photo" alt="profile picture" class="photo-container__img">
                                    <div class="upload-container">
                                        <button class="btn waves-effect waves-light upload-button" @click="showUploadDialog()">Upload new
                                            photo
                                        </button>
                                        <input class="upload-input" type="file" ref="file" v-on:change="uploadPhoto()" required>
                                    </div>
                                </div>
                                <a class="btn-floating halfway-fab waves-effect waves-light edit-profile-button"><i
                                        @click="editProfile()" class="material-icons">edit</i></a>
                            </div>
                            <div class="card-content">
                                    <span class="card-title">
                                        {{profile.name}}

                                        <span class="new badge red" data-badge-caption="Admin"
                                              v-if="profile.admin"></span>
                                        <span class="new badge blue" data-badge-caption="Volunteer"
                                              v-if="profile.volunteer"></span>
                                        <span class="new badge" data-badge-caption="Speaker"
                                              v-if="profile.speaker"></span>
                                    </span>

                            </div>
                            <div class="card-action">
                                <a href="#">New presentation</a>
                            </div>
                            <div class="card-content">
                                {{profile.bio}}
                            </div>

                            <div class="card-content">
                                <ul class="collection">
                                    <li class="collection-item"><i class="about-icon fab fa-twitter"></i>
                                        <span>{{profile.twitter}}</span><br/></li>
                                    <li class="collection-item"><i class="about-icon fab fa-github"></i>
                                        <span>{{profile.github}}</span><br/></li>
                                    <li class="collection-item"><i class="about-icon fas fa-home"></i> <span>{{profile.www}}</span><br/>
                                    </li>
                                    <li class="collection-item"><i class="about-icon fas fa-envelope"></i>
                                        <span>{{profile.email}}</span><br/></li>
                                </ul>

                            </div>
                        </div>
                    </div>

                    <div class="col s12 m6 l8">
                        <div class="card" v-for="pres in presentations">
                            <div class="card-content">
                                <span class="card-title">
                                    {{pres.title}}
                                    <span class="small">({{pres.language}}, {{pres.level}})</span>
                                      <span :data-badge-caption="tag.name" class="new badge"
                                            v-for="tag in pres.tags"></span>
                                </span>


                                full description:
                                <blockquote>{{pres.description}}</blockquote>
                                short description:
                                <blockquote>{{pres.shortDescription}}</blockquote>

                            </div>
                            <div class="card-action">
                                <a href="#">edit</a>
                            </div>
                        </div>

                    </div>
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
  import { EmbeddedPresentations, Presentation, UserProfile } from '@/types';
  import axios, { AxiosError } from 'axios';
  import PageHeader from '@/components/PageHeader.vue';
  import Toasted from 'vue-toasted';

  Vue.use(Toasted);

  @Component({
    components: { PageHeader, Box, TheContact },
  })
  export default class ProfilePage extends Vue {
    public $refs!: Vue['$refs'] & {
      file: {
        click: () => void,
        files: File[],
      },
    };
    public profile: UserProfile | null = null;
    public presentations: Presentation[] = [];
    public photoKey = 0;

    public mounted() {
      this.$store.dispatch(LOAD_CURRENT_PROFILE)
        .then(() => {
          this.profile = this.$store.state.userProfile.currentProfile;
        })
        .then(() => axios.get<EmbeddedPresentations>(`/api/users/${this.profile!.id}/presentations`))
        .then((response) => this.presentations = response.data._embedded.presentations);
    }

    public editProfile() {
      this.$router.push('/register');
    }

    public showUploadDialog() {
      this.$refs.file.click();
    }

    public uploadPhoto() {
      const { files } = this.$refs.file;
      const photo = files[0];
      if (photo !== null) {
        const formData = new FormData();
        formData.append('file', photo);
        return axios
          .post(`/api/resources/${this.profile!.id}`, formData)
          .then(() => this.photoKey += 1)
          .catch((error: AxiosError) => {
            let message = 'Ups... Something went wrong...';
            if (error.response!.status === 413) {
              message = 'Uploaded photo is too large!';
            }
            this.$toasted.error(message, { duration: 3000, className: 'error', fullWidth: true });
          });
      } else {
        throw new Error('Something went wrong');
      }
    }

  }
</script>

<style lang="scss" scoped>
    @import "../../assets/colors";

    .edit-profile-button {
        background-color: $brand;
    }

    .photo-container {
        display: flex;
        width: 400px;
        height: 400px;
        overflow: hidden;
        margin: auto;

        &__img {
            width: 100%;
            object-fit: cover;
            height: 100%;
        }
    }

    .upload-button {
        opacity: 0;

        &, &:hover, &:focus {
            background-color: $brand;
        }
    }

    .upload-input {
        display: none;
    }


    .upload-container {
        position: absolute;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        display: flex;
        justify-content: center;
        align-items: center;

        &:hover .upload-button {
            opacity: 0.7;
        }
    }

    .about-icon {
        margin-right: 1em;
    }
</style>
<style lang="scss">
    .toasted.error {
        font-size: 1rem;
    }

</style>
