<template>
  <div class="privacy-policy">
    <PageHeader title="Privacy Policy" class="header"> </PageHeader>
    <Box class="content" color="white">
      <PageFragment name="privacy-policy" />
    </Box>
    <TheContact id="contact" />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Box from "@/components/Box.vue";
import PageHeader from "@/components/PageHeader.vue";
import TheContact from "@/components/TheContact.vue";
import PageFragment from "@/components/PageFragment.vue";

@Component({
  components: { PageFragment, PageHeader, Box, TheContact }
})
export default class PrivacyPolicy extends Vue {}
</script>
