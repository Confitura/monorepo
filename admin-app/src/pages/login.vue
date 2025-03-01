<script setup lang="ts">
import router from "@/plugins/router.ts";

const name = ref('')

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
</script>

<template>
  <div class="wrapper">
    <v-icon icon="custom:confitura" size="3.5em" color="primary" class="mb-4"/>
    <p>Register/Login with chosen provider:</p>
    <v-container>
      <v-row align="center" justify="center">
        <v-col cols="auto">
          <v-btn icon="mdi-github" size="x-large"></v-btn>
        </v-col>
        <v-col cols="auto">
          <v-btn icon="mdi-google" size="x-large"></v-btn>
        </v-col>
        <v-col cols="auto">
          <v-btn icon="mdi-facebook" size="x-large"></v-btn>
        </v-col>
        <v-col cols="auto">
          <v-btn icon="mdi-alpha-x" size="x-large"></v-btn>
        </v-col>
      </v-row>
    </v-container>
    <v-responsive max-width="300" class="mx-auto">
      <v-text-field
          v-model="name"
          color="primary"
          placeholder="Hello World"
          label="What's your name?"
          class="mx-auto mt-8"
      />
    </v-responsive>
    <v-btn :disabled="!name" class="mr-2" color="primary" @click="sayHi">
      Login
    </v-btn>
    <v-btn :disabled="!name" @click="warning">Cancel</v-btn>
  </div>
</template>

<style scoped>
.wrapper {
  position: relative;
  top: calc(50vh - 330px);
  text-align: center;
}
</style>
