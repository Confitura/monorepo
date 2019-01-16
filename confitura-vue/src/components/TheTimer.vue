<template>
    <div class="timer" :key="seconds">
        <div class="timer-part">
            <span class="timer-value">{{days  | pad}}</span>
            <span class="timer-unit">days</span>
        </div>
        <span class="timer-part-separator"></span>
        <div class="timer-part">
            <span class="timer-value">{{hours  | pad}}</span>
            <span class="timer-unit">hours</span>
        </div>
        <span class="timer-part-separator"></span>
        <div class="timer-part">
            <span class="timer-value">{{minutes  | pad}}</span>
            <span class="timer-unit">minutes</span>
        </div>
        <span class="timer-part-separator"></span>
        <div class="timer-part">
            <span class="timer-value">{{seconds | pad}}</span>
            <span class="timer-unit">seconds</span>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import dayjs from "dayjs";

    @Component({
        filters: {
            pad(value: number) {
                return `${value}`.length == 1 ? `0${value}` : value;
            }
        }
    })
    export default class TheTimer extends Vue {
        private handle: number = 0;
        start = dayjs("2019-06-30T09:00");
        now: dayjs.Dayjs = dayjs();
        days = 0;
        hours = 0;
        minutes = 0;
        seconds = 0;

        mounted() {
            this.recalculate();
            this.handle = setInterval(() => {
                this.recalculate();
            }, 1000);
        }

        beforeDestroy() {
            clearInterval(this.handle);
        }

        private recalculate() {
            this.now = dayjs();
            this.days = this.start.diff(this.now, "day");
            this.hours = this.start
                .subtract(this.days, "day")
                .diff(this.now, "hour");
            this.minutes = this.start
                .subtract(this.days, "day")
                .subtract(this.hours, "hour")
                .diff(this.now, "minute");
            this.seconds = this.start
                .subtract(this.days, "day")
                .subtract(this.hours, "hour")
                .subtract(this.minutes, "minute")
                .diff(this.now, "second");
        }

    }
</script>

<style scoped lang="scss">
    .timer {
        margin-top: 50px;
        color: #ee1f46;
        display: flex;
    }

    .timer-part {
        display: flex;
        flex-direction: column;
    }

    .timer-value {
        font-size: 3rem;
        font-weight: bold;
    }

    .timer-part-separator:after {
        content: ':';
        font-size: 3rem;
        font-weight: bold;
        margin-left: 10px;
        margin-right: 10px;
    }

    .timer-unit {

    }
</style>