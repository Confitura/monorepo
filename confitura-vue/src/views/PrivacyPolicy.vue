<template>
  <div class="privacy-policy">
    <PageHeader title="Privacy Policy" class="header"> </PageHeader>
    <Box class="content" color="white">
      <PageFragment name="privacy-policy" />
    </Box>
    <Contact />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Box from "@/components/Box.vue";
import PageHeader from "@/components/PageHeader.vue";
import Contact from "@/components/Contact.vue";
import PageFragment from "@/components/PageFragment.vue";

@Component({
  components: { PageFragment, PageHeader, Box, Contact }
})
export default class PrivacyPolicy extends Vue {}
</script>
