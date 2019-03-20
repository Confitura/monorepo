<template>
    <div class="login">
        <Box class="content" color="white">
            <h1>Login with</h1>
            <div class="login-with">
                <a v-for="option in options" :key="option.name" :href="loginLinkTo(option.name)">
                    <i class="login__icon fab" :class="'fa-'+option.name"></i>
                </a>
            </div>
        </Box>
        <TheContact id="contact"/>
    </div>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import Box from '@/components/Box.vue';
  import TheContact from '@/components/TheContact.vue';
  import { LOGIN } from '@/types';

  @Component({
    components: { Box, TheContact },
  })
  export default class Login extends Vue {
    public options = [
      { name: 'twitter' },
      { name: 'facebook' },
      { name: 'google' },
      { name: 'github' },
    ];

    public loginLinkTo(service: string) {
      return `/api/login/${service}`;
    }

    protected mounted() {
      const service = this.$route.params['service'];
      if (service) {
        const params = {
          'code': this.$route.query['code'],
          'oauth_token': this.$route.query['oauth_token'],
          'oauth_verifier': this.$route.query['oauth_verifier'],
        };
        this.$store.dispatch(LOGIN, { service, params });
      }
    }
  }

</script>

<style lang="scss" scoped>
    @import "../assets/colors";
    @import "../assets/sizes";
    @import "../assets/media";
    @import "../assets/fonts";

    .login-with {
        font-size: 2rem;
        display: flex;
    }

    .login__icon {
        padding: 0.5rem;
    }

</style>
