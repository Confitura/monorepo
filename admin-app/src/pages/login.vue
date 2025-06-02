<script setup lang="ts">
import router from "@/plugins/router.ts";

const name = ref('')
const apiUrl = import.meta.env.VITE_API_URL;
const selfUrl = import.meta.env.VITE_SELF_URL;

function sayHi() {
  localStorage.setItem('token', name.value)
  router.push('/')
}

function warning() {
  Notify.warning(`How dare you refuse me, ${name.value}.`)
  localStorage.removeItem('token')
}

definePage({
  meta: {
    icon: 'mdi-home',
    title: 'Login',
    drawerIndex: 0,
    skipMenu: true,
    layout: 'whole-page',
  },
})

function loginUrl(provider: string) {

  const url = new URL(`${apiUrl}/login/${provider}`);
  url.searchParams.set('redirect_uri', `${selfUrl}/login/${provider}`);
  url.searchParams.set('state', "static-state");
  return url.toString();
}
</script>

<template>
  <v-container class="container">

    <v-row align="center" justify="center">
      <v-col cols="auto">
        <p>By registering you accept our <a href="/privacy-policy">privacy
          policy</a>:</p>
      </v-col>
    </v-row>
    <v-row align="center" justify="center">
      <v-col cols="auto">
        <v-btn icon="mdi-github" size="x-large"
               :href="loginUrl('github')"></v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn icon="mdi-google" size="x-large"
               :href="loginUrl('google')"></v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn icon="mdi-facebook" size="x-large"
               :href="loginUrl('facebook')"></v-btn>
      </v-col>
      <v-col cols="auto">
        <v-btn icon="mdi-alpha-x" size="x-large"
               :href="loginUrl('twitter')"></v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-container>
          <v-row align="center" justify="center">
            <v-col cols="auto">
              <v-icon icon="custom:confitura" size="2.5em" color="primary"
                      class="mb-4"/>
            </v-col>
          </v-row>

        </v-container>
      </v-col>
    </v-row>
    <v-row class="mb-12" justify="center">
      <v-col cols="12" xl="4" md="6" sm="12"  align-self="center">
      <div class="info">
          <p>Hey You,</p>

          <p>Thanks for deciding to join Confitura in 2025!</p>

          <p>First things first: this year, the conference will be two days long
            -
            Friday and Saturday (19th and 20th of September, 2025).</p>

          <p>We accept both talks and workshops, so there are plenty of
            opportunities to share your knowledge.
            Regular talks are 60 minutes long, and workshops can be 3 hours or
            longer (just keep human limits in mind - please don’t send us a
            12-hour
            proposal, lol).
            You can submit as many abstracts as you like.
            If you have recordings of your past public appearances at other IT
            events, please include a link in your submission - it really helps
            us
            get to know our future speakers better.
            <b> The deadline</b> to submit your abstract is <b>July 31.</b></p>

          <p>How are talks selected for the Confitura agenda? We’ve let the
            community decide for years. After the Call for Papers deadline, we
            invite people to vote on the submissions, and based on the
            community’s
            picks, we shape the agenda.</p>
        </div>
      </v-col>
    </v-row>
  </v-container>

</template>

<style scoped>
.wrapper {

  margin: auto;
  position: relative;
  top: calc(50vh - 330px);
  text-align: center;
}

.info {
  text-align: justify;
}

.info p {
  margin-bottom: 1rem;
}


</style>
