<template>
    <div class="participant">
        <PageHeader title="Registration" type="peace"></PageHeader>
        <Box :full="false" color="white">
            <div v-if="participant" class="confirmation">
                <h2 class="header">Success!</h2>
                <div class="message">
                    <h4>
                        Congrats {{participant.firstName}} {{participant.lastName}} !!!
                    </h4>
                    <p>You are all setup to come to Confitura 2019!</p>
                    <p>Want to get a maximum from Confitura experience? If you sign up into our system,
                        you will be able to create personal agenda, and rate presentations as you hear them!</p>
                    <p>
                        Click
                        <router-link to="/login">here</router-link>
                        to do so.
                    </p>
                    <p>
                        Here is your ticket.
                    </p>
                    <div class="barCode">
                        <img :src="qrcode" alt="b0999fbb-408c-4442-93fb-5b81e8a3265a" v-if="qrcode"/>
                    </div>
                    <p>
                        Show it at the registration desk and enjoy the conference! You can save current URL in your browser (or wherever you want) to return to
                        this page any time.<br/>
                        But don't worry if you don't do that - our small, magical, helpers are just preparing an email for you that will include your
                        qr-code as well.<br/>
                        If you dont get that e-mail in few minutes then it means that they did very poor job. It's not us - it's them, but
                        still - sorry!<br/>
                        Before contacting us, please make sure that our e-mail did not end up in spam, or any other folder. <br/>
                        Please also make sure that the e-mail address that you have registered ({{participant.email}}) is the correct one.
                    </p>
                    <p>Other than that... see you on <strong>Saturday, June 26th 2019</strong>!!</p>


                </div>
            </div>
        </Box>
    </div>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import Box from '@/components/Box.vue';
  import TheContact from '@/components/TheContact.vue';
  import axios from 'axios';
  import PageHeader from '@/components/PageHeader.vue';
  import { Participant, PARTICIPATION_ID } from '@/types';

  @Component({
    components: { PageHeader, Box, TheContact },
  })
  export default class ParticipantPage extends Vue {

    public participant: Participant | null = null;
    public qrcode: string = '';

    public mounted() {
      const { id } = this.$route.params;
      axios.get<Participant>(`/api/participants/${id}`)
        .then((it) => {
          localStorage.setItem(PARTICIPATION_ID, id);
          this.participant = it.data;
          this.qrcode = `/api/participants/${this.participant.id}/ticket`;
        });
    }
  }
</script>

<style lang="scss" scoped>
    @import "../../assets/colors";
    @import "../../assets/sizes";
    @import "../../assets/media";
    @import "../../assets/fonts";

    .confirmation {
        display: flex;
        flex-direction: column;
        justify-content: space-around;
        @include md() {
            flex-direction: row;
        }
    }

    .header {
        color: $brand;
        font-size: 3rem;
        font-weight: bold;
        flex-basis: 30%;
        margin-right: 3rem;
    }

    .message {
        flex-basis: 50%;
        font-size: 1.3rem;
        line-height: 1.5rem;
    }

    .barCode {
        text-align: center;
    }

</style>
