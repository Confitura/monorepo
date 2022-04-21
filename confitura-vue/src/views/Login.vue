<template>
  <div class="login">
    <PageHeader :small="true" title="Call 4 Papers"></PageHeader>
    <Box class="content" color="white" :full="false">
      <p class="login__info">
        To register your presentation with our Call 4 Papers process you need to create a profile. Your presentation will be presented to all V4P participants as is - so make sure that it is as polished as possible.
        <br/>
        <br/>
        C4P will be closed on May 21, 2022. If you have any questions about presentations - please make sure to check our <router-link to="faq" class="link--red">FAQ</router-link>.
      </p>
      <div class="login-container">
        <div v-for="option in options" :key="option" class="login-with">
          <h2 class="option-header">{{ option }}</h2>
          <a
            v-for="service in services"
            :key="service.name"
            class="link"
            :class="`link--${service.name}`"
            :href="loginLinkTo(service.name)"
          >
            <i class="login__icon fab" :class="'fa-' + service.name"></i>
            <span class="login__name"
              >{{ option }} with {{ service.name }}</span
            >
          </a>
        </div>
      </div>
    </Box>
    <Contact />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Box from "@/components/Box.vue";
import Contact from "@/components/Contact.vue";
import { LOGIN } from "@/types";
import PageHeader from "@/components/PageHeader.vue";

@Component({
  components: { Box, Contact, PageHeader }
})
export default class Login extends Vue {
  public options = ["Sign in", "Sign up"];
  public services = [
    { name: "twitter" },
    { name: "facebook" },
    { name: "google" },
    { name: "github" }
  ];

  public loginLinkTo(service: string) {
    return `/api/login/${service}`;
  }

  protected mounted() {
    const { service } = this.$route.params;
    if (service) {
      const { code, oauth_token, oauth_verifier } = this.$route.query;
      const params = { code, oauth_token, oauth_verifier };
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

.login {
  .login__info {
    font-size: 1.5rem;
  }

  .login-container {
    display: grid;
    grid-gap: 1rem;
    grid-template-columns: 1fr;
    justify-items: center;

    @include md() {
      grid-template-columns: 1fr 1fr;
    }
  }

  .login-with {
    font-size: 1.5rem;
    display: flex;
    flex-direction: column;
    max-width: 350px;
  }

  .login__icon {
    padding-right: 1rem;
    padding-left: 0.5rem;
    border-right: 1px solid rgba(0, 0, 0, 0.2);
  }

  .login__name {
    padding-top: 0.3rem;
    padding-left: 1rem;
  }

  & .link {
    text-decoration: none;
    color: #ffffff;
    border-radius: 0.5rem;
    display: flex;
    align-items: center;
    padding: 0.5rem;
    margin-bottom: 0.5rem;

    &:hover {
      opacity: 0.83;
      color: #ffffff;
    }

    &--twitter {
      background-color: #55acee;
    }

    &--facebook {
      background-color: #3b5998;
    }

    &--github {
      background-color: #444;
    }

    &--google {
      background-color: #dd4b39;
    }
  }
}
</style>
