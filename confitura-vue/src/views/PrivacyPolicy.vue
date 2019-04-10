<template>
    <div class="privacy-policy">
        <PageHeader title="Privacy Policy" class="header">
        </PageHeader>
        <Box class="content" color="white">
            <section class="privacy-policy__content" v-html="content"></section>
        </Box>
        <TheContact id="contact"/>
    </div>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import Box from '@/components/Box.vue';
  import PageHeader from '@/components/PageHeader.vue';
  import TheContact from '@/components/TheContact.vue';
  import { Page } from '@/types';
  import axios from 'axios';
  import showdown from 'showdown';


  @Component({
    components: { PageHeader, Box, TheContact },
  })
  export default class PrivacyPolicy extends Vue {
    public content = '';
    private converter = new showdown.Converter();

    private mounted() {
      axios.get<Page>('/api/pages/privacy-policy')
        .then((response) => response.data.content)
        .then((content: string) => this.content = this.converter.makeHtml(content));

    }
  }

</script>
