<template>
  <div class="faq">
    <PageHeader title="Lean Coffee"/>
    <Box class="content" color="white">
      <PageFragment name="lean-coffee"/>
    </Box>
    <Contact/>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import Box from "@/components/Box.vue";
import PageHeader from "@/components/PageHeader.vue";
import Contact from "@/components/Contact.vue";
import PageFragment from "@/components/PageFragment.vue";

@Component({
  components: { PageHeader, Box, Contact, PageFragment }
})
export default class LeanCoffee extends Vue {
}
</script>

<style lang="scss" scoped>
@import "../assets/colors";
@import "../assets/sizes";
@import "../assets/media";
@import "../assets/fonts";

.faq {
  overflow: hidden;
}

.content {
  .foreword {
    text-align: left;
    color: #000000;
    font-size: 1.5rem;
    @include md() {
      width: 50%;
    }

    &__link {
      color: $brand;
    }
  }
}
</style>
<style lang="scss">
@import "../assets/colors";
@import "../assets/sizes";
@import "../assets/media";
@import "../assets/fonts";

</style>
