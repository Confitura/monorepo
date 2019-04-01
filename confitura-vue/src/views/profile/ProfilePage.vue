<template>
    <div class="profile">
        <Box class="content" color="white">
            <div v-if="profile">
                YOU ARE LOGGED IN
            </div>
        </Box>
        <TheContact id="contact"/>
    </div>
</template>

<script lang="ts">
import {Component, Vue} from 'vue-property-decorator';
import {LOAD_CURRENT_PROFILE} from '@/store.user-profile';
import Box from '@/components/Box.vue';
import TheContact from '@/components/TheContact.vue';
import {UserProfile} from '@/types';

@Component({
  components: {Box, TheContact},
})
export default class ProfilePage extends Vue {
  public profile: UserProfile | null = null;

  public mounted() {

    this.$store.dispatch(LOAD_CURRENT_PROFILE)
      .then(() => {
        this.profile = this.$store.state.userProfile.currentProfile;
      });
  }

}
</script>

<style lang="scss" scoped>

</style>
