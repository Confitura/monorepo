<template>
  <div class="scanner">
    <Box :full="false" :small="true" class="content back-office" color="white">
      <div class="card-panel red error" v-if="error">{{ error }}</div>
      <div v-if="participant">
        <div class="card">
          <div class="card-content ">
            <span class="card-title">
              {{ participant.voucher.type }}
              <small>{{ participant.voucher.comment }}</small>
            </span>

            <p class="subtitle">
              {{ participant.name }} {{ participant.email }}
            </p>
            <div>
              t-shirt
              <div class="scanner__tshirt">
                <span v-if="participant.gender === 'm'">Męska</span>
                <span v-else>Damska</span>
                {{ participant.size }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col m6 s12">
          <div class="input-field">
            <input
              id="id"
              type="text"
              placeholder="enter code using keyboard code scanner"
              v-model="inputValue"
            />
            <label for="id"></label>
          </div>
        </div>
      </div>
      <md-button class="md-fab scanner__scanButton" @click="openScanner()">
        <md-icon>center_focus_strong</md-icon>
      </md-button>
    </Box>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from "vue-property-decorator";
import Box from "@/components/Box.vue";
import PageHeader from "@/components/PageHeader.vue";
import Contact from "@/components/Contact.vue";
import axios from "axios";
import { Participant } from "@/types";

@Component({
  components: { PageHeader, Box, Contact }
})
export default class Scanner extends Vue {
  public inputValue: string = "";
  public participant: Participant | null = null;
  public error: string | null = null;

  get id() {
    const { id } = this.$route.params;
    return id;
  }

  public openScanner() {
    window.location.href =
      "zxing://scan/?ret=https://2023.confitura.pl/scanner/{CODE}&SCAN_FORMATS=QR_CODE";
  }

  public mounted() {
    this.scan(this.id);
  }

  @Watch("inputValue")
  public inputChanged(event: string) {
    this.$router.push({
      name: "scanner",
      params: { id: event }
    });
  }

  @Watch("id")
  public idChanged(value: string) {
    this.scan(value);
  }

  public scan(id: string) {
    if (id) {
      this.participant = null;
      this.error = null;
      this.inputValue = "";
      axios
        .post<Participant>(`/api/participants/${id}/arrived`, {})
        .then(value => {
          this.participant = value.data;
        })
        .catch(reason => {
          if (reason.response.status === 409) {
            this.error = "Już zarejestrowany!";
            this.participant = reason.response.data;
          } else {
            this.error = "Niepoprawny token";
          }
        });
    }
  }
}
</script>

<style scoped lang="scss">
.scanner {
}

.back-office {
  padding-top: 100px;
}

.back-office span.badge.new:after {
  content: "";
}

.back-office span.badge {
  min-width: 2rem;
}

.card-content {
  .subtitle {
    font-weight: bold;
    font-size: 1.5rem;
  }

  .scanner__tshirt {
    font-size: 2.5rem;
  }
}

.error {
  font-size: 2.5rem;
}

.scanner__scanButton {
  position: fixed;
  bottom: 2rem;
  right: 2rem;
}
</style>
