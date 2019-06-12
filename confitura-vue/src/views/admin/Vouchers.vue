<template>
    <div class="vouchers">

        <div class="generateVouchers">


            <div class="input-field">
                <input id="email" type="email"
                       v-model="email">
                <label for="email">E-Mail</label>
            </div>
            <div class="input-field">
                <input id="comment" type="text"
                       v-model="comment">
                <label for="comment">Comment (eg. sponsor name)</label>
            </div>
            <div class="input-field">
                <input id="sponsorVouchers" type="number"
                       v-model="sponsorVouchers">
                <label for="sponsorVouchers">Sponsor Vouchers</label>
            </div>
            <div class="input-field">
                <input id="participantVouchers" type="number"
                       v-model="participantVouchers">
                <label for="participantVouchers">Participant Vouchers</label>
            </div>


            <button @click="generateVouchers()">generate</button>
            info: generated ticket are not send - to send tickets to participants use file import (from old console)
        </div>
        <div class="generatedVouchers" v-if="generatedVouchers.length>0">
            Generated {{generatedVouchers.length}} vouchers:
            <table>
                <thead>
                <tr>
                    <th>link</th>
                    <th>buyer</th>
                    <th>type</th>
                </tr>
                </thead>
                <tr v-for="voucher of generatedVouchers">
                    <td>
                        {{page}}/participate?voucher={{voucher.id}}
                    </td>
                    <td>{{voucher.originalBuyer}}</td>
                    <td>{{voucher.type}}</td>

                </tr>
            </table>
        </div>
        <table>
            <thead>

            <tr>
                <th>link</th>
                <th>buyer</th>
                <th>email sent</th>
                <th>type</th>
                <th>comment</th>
            </tr>
            </thead>
            <tr v-for="voucher of vouchers">
                <td>
                    {{page}}/participate?voucher={{voucher.id}}
                </td>
                <td>{{voucher.originalBuyer}}</td>
                <td>{{voucher.emailSent}}</td>
                <td>{{voucher.type}}</td>
                <td>{{voucher.comment}}</td>

            </tr>
        </table>
    </div>
</template>


<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { Voucher } from '@/types';
import axios from 'axios';
import { LOAD_VOUCHERS } from '@/store/admin';

@Component({
  components: {},
})
export default class Vouchers extends Vue {

  public email: string | null = null;
  public comment: string | null = null;
  public sponsorVouchers: number = 0;
  public participantVouchers: number = 0;

  public generatedVouchers: Voucher[] = [];
  public page: any;

  public mounted() {
    this.page = window.location.origin;
  }

  get vouchers() {
    return this.$store.state.admin.vouchers;
  }

  public generateVouchers() {
    axios.post<Voucher[]>('/api/vouchers', {
      email: this.email,
      comment: this.comment,
      sponsorVouchers: this.sponsorVouchers,
      participantVouchers: this.participantVouchers,
    })
      .then((it) => this.generatedVouchers = it.data)
      .then(() => this.$store.dispatch(LOAD_VOUCHERS));
  }


}


interface GenerateVouchersRequest {
  email: string;
  sponsorVouchers: number;
  participantVouchers: number;
}
</script>
