<template>
  <div class="vouchers">
    <div class="vouchersForm">
      <div class="generateVouchers">
        <div class="input-field">
          <input id="email" type="email" v-model="email" />
          <label for="email">E-Mail</label>
        </div>
        <div class="input-field">
          <input id="comment" type="text" v-model="comment" />
          <label for="comment">Comment (eg. sponsor name)</label>
        </div>
        <div class="input-field">
          <input id="sponsorVouchers" type="number" v-model="sponsorVouchers" />
          <label for="sponsorVouchers">Sponsor Vouchers</label>
        </div>
        <div class="input-field">
          <input
            id="participantVouchers"
            type="number"
            v-model="participantVouchers"
          />
          <label for="participantVouchers">Participant Vouchers</label>
        </div>

        <button
          class="waves-effect waves-light btn"
          @click="generateVouchers()"
        >
          generate
        </button>

        Generated vouchers are not send by email! To send emails use file
        upload;
      </div>
      <div class="generatedVouchers" v-if="generatedVouchers.length > 0">
        Generated {{ generatedVouchers.length }} vouchers:
        <table>
          <thead>
            <tr>
              <th>link</th>
              <th>buyer</th>
              <th>type</th>
            </tr>
          </thead>
          <tr v-for="voucher of generatedVouchers" :key="voucher.id">
            <td>{{ page }}/participate?voucher={{ voucher.id }}</td>
            <td>{{ voucher.originalBuyer }}</td>
            <td>{{ voucher.type }}</td>
          </tr>
        </table>
      </div>
    </div>
    <div class="vouchersUpload">
      <div>
        <input type="file" ref="file" hidden @change="refreshFile" />
        <button class="waves-effect waves-light btn" @click="selectFile">
          Select File
        </button>
        <button
          class="waves-effect waves-light btn"
          @click="submitFile"
          :disabled="!selectedFile && !loadingInProgress"
        >
          Upload {{ selectedFile }}!
        </button>
        <span v-if="loadingInProgress" class="loadingInProgress">
          UPLOADING...
        </span>
      </div>
      <div v-if="uploadResult.length > 0">
        <table>
          <thead>
            <tr>
              <th>buyerEmail</th>
              <th>successCount</th>
              <th>requestedCount</th>
              <th>type</th>
              <th>comment</th>
            </tr>
          </thead>
          <tr
            v-for="voucher of uploadResult"
            :key="voucher.buyerEmail + voucher.requestedCount"
          >
            <td>{{ voucher.buyerEmail }}</td>
            <td>{{ voucher.successCount }}</td>
            <td>{{ voucher.requestedCount }}</td>
            <td>{{ voucher.type }}</td>
            <td>{{ voucher.comment }}</td>
          </tr>
        </table>
      </div>
    </div>
    <div>
      <button class="waves-effect waves-light btn" @click="sendVouchers">
        Send vouchers
      </button>
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
      <tr v-for="voucher of vouchers" :key="voucher.id">
        <td>{{ page }}/participate?voucher={{ voucher.id }}</td>
        <td>{{ voucher.originalBuyer }}</td>
        <td>{{ voucher.emailSent }}</td>
        <td>{{ voucher.type }}</td>
        <td>{{ voucher.comment }}</td>
      </tr>
    </table>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { Voucher } from "@/types";
import axios from "axios";
import M from "materialize-css";
import { LOAD_VOUCHERS } from "@/store/admin";

@Component({
  components: {}
})
export default class Vouchers extends Vue {
  public $refs!: Vue["$refs"] & {
    file: any;
  };

  public email: string | null = null;
  public comment: string | null = null;
  public sponsorVouchers: number = 0;
  public participantVouchers: number = 0;

  public generatedVouchers: Voucher[] = [];
  public page: any = "";

  public selectedFile: string = "";
  public uploadResult: UploadVouchersStatus[] = [];
  public loadingInProgress = false;

  public mounted() {
    this.page = window.location.origin;
    M.updateTextFields();
  }

  public updated() {
    M.updateTextFields();
  }

  get vouchers() {
    return this.$store.state.admin.vouchers;
  }

  selectFile() {
    this.$refs.file.click();
  }

  refreshFile() {
    if (this.$refs.file.files) {
      let file = this.$refs.file.files[0];
      this.selectedFile = file.name;
    } else {
      this.selectedFile = "";
    }
  }

  submitFile() {
    let file = this.$refs.file;
    const formData = new FormData();
    formData.append("file", file.files[0]);
    const headers = { "Content-Type": "multipart/form-data" };
    this.loadingInProgress = true;
    axios
      .post<UploadVouchersStatus[]>("/api/participants/upload", formData, {
        headers
      })
      .then(res => {
        this.uploadResult = res.data;
        this.loadingInProgress = false;
        this.selectedFile = "";
      })
      .catch(() => {
        this.loadingInProgress = false;
      });
  }

  sendVouchers() {
    axios.post<Voucher[]>("/api/vouchers/send");
  }

  public generateVouchers() {
    axios
      .post<Voucher[]>("/api/vouchers", {
        email: this.email,
        comment: this.comment,
        sponsorVouchers: this.sponsorVouchers,
        participantVouchers: this.participantVouchers
      })
      .then(it => (this.generatedVouchers = it.data))
      .then(() => this.$store.dispatch(LOAD_VOUCHERS));
  }
}

interface GenerateVouchersRequest {
  email: string;
  sponsorVouchers: number;
  participantVouchers: number;
}

interface UploadVouchersStatus {
  buyerEmail: string;
  successCount: number;
  requestedCount: number;
  type: string;
  comment: string;
}
</script>
<style>
.generatedVouchers {
  margin-top: 2em;
  margin-bottom: 2em;
}

.loadingInProgress {
  padding: 3em;
}
</style>
