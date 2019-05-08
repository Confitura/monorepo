<template>
    <div class="profile">
        <PageHeader :small="true" title="My Profile"></PageHeader>
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
                                <router-link :to="{name:'register', params:{id: profile.id}}">
                                    Edit profile
                                </router-link>
                                <router-link :to="{name:'presentation', params:{userId: profile.id}}">
                                    Add presentation
                                </router-link>
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
                        <!-- Modal Structure For adding speaker -->
                        <div class="modal" id="modal1">
                            <div class="modal-content" v-if="selectedPresentation">
                                <h4>{{selectedPresentation.title}}</h4>
                                <div class="input-field ">
                                    <input id="email" type="email" v-model="email">
                                    <label for="email">Email of speaker</label>
                                    <span class="helper-text" data-error="wrong" data-success="right">Speaker {{email}} must be already registered in system</span>
                                </div>
                                <button @click="addSpeakerToPresentation()" class="btn">
                                    Add speaker
                                </button>
                            </div>
                            <div class="modal-footer">
                                <a class="modal-close waves-effect waves-green btn-flat">close</a>
                            </div>
                        </div>
                        <div class="card" v-for="presentation in presentations">
                            <div class="card-content">
                                <div class="card-title">
                                    <div>
                                        {{presentation.title}}
                                        <span class="small">({{presentation.language}}, {{presentation.level}})</span>
                                    </div>
                                    <div class="tags">
                                <span :data-badge-caption="tag.name" class="new badge"
                                      v-for="tag in presentation.tags"></span>
                                    </div>
                                </div>


                                <label>Short description</label>
                                <div class="description">{{presentation.shortDescription}}</div>

                                <label>Description</label>
                                <div class="description">{{presentation.description}}</div>

                                <div v-if="presentation.speakers.length > 1">
                                    Speakers:
                                    <ul class="collection">
                                        <li class="collection-item" v-for="speaker of presentation.speakers">
                                        <span class="title">
                                            {{speaker.name}}
                                        </span>
                                            <a @click="deleteSpeaker(presentation, speaker, $event)" class="secondary-content"
                                               href="#">
                                                <i class="material-icons">delete</i></a>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                            <div class="card-action">
                                <router-link :to="{name: 'presentation', params:{id:presentation.id}}">edit</router-link>
                                <a href="#" @click="remove(presentation, $event)">delete</a>
                                <button @click="openModal(presentation)" class="btn modal-trigger" data-target="modal1">
                                    Add speakers
                                </button>
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
  import { LOAD_PROFILE_BY_ID, LOAD_PROFILE_PRESENTATIONS_BY_ID } from '@/store/store.user-profile';
  import Box from '@/components/Box.vue';
  import TheContact from '@/components/TheContact.vue';
  import { EmbeddedPresentations, Presentation, REMOVE_PRESENTATION, UserProfile } from '@/types';
  import axios, { AxiosError } from 'axios';
  import PageHeader from '@/components/PageHeader.vue';
  import Toasted from 'vue-toasted';
  import M from 'materialize-css';

  Vue.use(Toasted);

  @Component({
    components: { PageHeader, Box, TheContact },
  })
  export default class ProfilePage extends Vue {

    get profile() {
      return this.$store.state.userProfile.currentProfile;
    }

    get presentations() {
      return this.$store.state.userProfile.currentProfilePresentations;
    }

    public $refs!: Vue['$refs'] & {
      file: {
        click: () => void,
        files: File[],
      },
    };

    public photoKey = 0;
    public selectedPresentation: Presentation | null = null;
    public email = '';

    public mounted() {
      this.reloadData();
    }

    private reloadData() {
      const userId = this.$route.params.id || this.$store.getters.user.jti;
      this.$store.dispatch(LOAD_PROFILE_BY_ID, { id: userId });
      this.$store.dispatch(LOAD_PROFILE_PRESENTATIONS_BY_ID, { id: userId });
    }

    public addSpeakerToPresentation() {
      const userId = this.$route.params.id || this.$store.getters.user.jti;
      axios.post(`/api/presentations/${this.selectedPresentation!.id}/cospeakers/${this.email}`)
        .then((it) => {
          this.reloadData();
          this.$toasted.success('speaker added', { duration: 3000 });
          this.closeModal();
        }, (error: AxiosError) => {
          let message = 'Something went wrong';
          if (error.response!.status === 404) {
            message = 'User not found';
          } else if (error.response!.status === 409) {
            message = 'Unable to add this user: conflict';
          }
          this.$toasted.error(message, { duration: 3000, className: 'error', fullWidth: true });
        });
    }

    public deleteSpeaker(pres: Presentation, speaker: UserProfile, event: Event) {
      event.preventDefault();
      if ((this.profile!.email !== speaker.email)
        || confirm('' +
          'Are you sure you want to remove yourself from presentation? ' +
          'You will no longer be able to change it.')) {
        axios.delete(`/api/presentations/${pres.id}/cospeakers/${speaker.email}`)
          .then((it) => {
            this.reloadData();
          });
      }
    }

    public openModal(pres: Presentation) {
      this.selectedPresentation = pres;
      const elems = document.querySelector('.modal');
      M.Modal.init(elems!);
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

    public remove(presentation: Presentation, event: Event) {
      event.preventDefault();
      this.$store.dispatch(REMOVE_PRESENTATION, presentation.id)
        .then(() => this.$delete(this.presentations, this.presentations.indexOf(presentation)));
    }

    private closeModal() {
      const elem = document.querySelector('.modal');
      M.Modal.getInstance(elem!).close();
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
        width: 250px;
        height: 250px;
        overflow: hidden;
        margin: auto;

        &__img {
            width: 100%;
            object-fit: cover;
            height: 100%;
            padding: 0.5rem;
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

    .description {
        margin-bottom: 1rem;
        padding-left: 1rem;
        white-space: pre-line;
    }

    .tags {
        display: flex;
    }
</style>
<style lang="scss">
    .toasted.error {
        font-size: 1rem;
    }

</style>
