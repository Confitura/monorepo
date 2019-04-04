<template>
    <div class="profile">
        <Box class="content" color="white">
            <div class="back-office" v-if="profile">
                <div class="row">

                    <div class="col s12 m6 l4">
                        <div class="card">
                            <div class="card-image">
                                <img :src="profile.photo" alt="profile picture">
                                <a class="btn-floating halfway-fab waves-effect waves-light red"><i
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
import {Component, Vue} from 'vue-property-decorator';
import {LOAD_CURRENT_PROFILE} from '@/store/store.user-profile';
import Box from '@/components/Box.vue';
import TheContact from '@/components/TheContact.vue';
import { UserProfile, EmbeddedPresentations, Presentation } from '@/types';
import axios from 'axios';


@Component({
  components: {Box, TheContact},
})
export default class ProfilePage extends Vue {
  public profile: UserProfile | null = null;
  public presentations: Presentation[] = [];

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

}
</script>

<style lang="scss" scoped>
    .about-icon {
        margin-right: 1em;
    }
</style>
