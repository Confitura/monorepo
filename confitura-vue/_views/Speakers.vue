<template>
  <div class="speakers">
    <PageHeader title="Our speakers" type="coder" />
    <Box class="content no-padding" color="white">
      <UsersGrid :users="speakers" class="speakers__grid" />
    </Box>
    <Contact />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Box from "@/components/Box.vue";
import PageHeader from "@/components/PageHeader.vue";
import Contact from "@/components/Contact.vue";
import { LOAD_SPEAKERS } from "@/store/admin";
import UsersGrid from "@/views/UsersGrid.vue";

@Component({
  components: { UsersGrid, PageHeader, Box, Contact }
})
export default class Speakers extends Vue {
  public mounted() {
    this.$store.dispatch(LOAD_SPEAKERS);
  }

  public get speakers() {
    return this.$store.state.admin.speakers;
  }
}
</script>

<style lang="scss" scoped>
@import "../assets/colors";
@import "../assets/sizes";
@import "../assets/media";
@import "../assets/fonts";

.speakers {
  overflow: hidden;

  &__grid {
    margin-top: 4rem;
  }
}
</style>
