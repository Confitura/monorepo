<template>
    <Box class="numbers" color="red">
        <div class="numbers-container">
            <div class="info">
                <h1 class="header">
                    <div>confitura'18</div>
                    <div>in numbers</div>
                </h1>
                <div class="subinfo">
                    Tutaj jakiś krótki tekst o tym jak kształtowała się confitura na przestrzeni lat i czym jest dla organizatorów,
                    partnerów i
                    uczestników.
                </div>

            </div>
            <Pictogram
                    :src="require('../assets/speakers.svg')"
                    value="38"
                    unit="speakers"
            />
            <Pictogram
                    :src="require('../assets/sessions.svg')"
                    value="5"
                    unit="sessions"
            />
            <Pictogram
                    :src="require('../assets/presentations.svg')"
                    value="35"
                    unit="presentations"
            />
            <Pictogram
                    :src="require('../assets/views.svg')"
                    value="693k"
                    unit="youtube channel views"
            />
            <Pictogram
                    :src="require('../assets/videos.svg')"
                    value="241"
                    unit="youtube videos"
            />
            <Pictogram
                    :src="require('../assets/participants.svg')"
                    value=">1800"
                    unit="participants"
            />
        </div>
    </Box>
</template>

<script>
  import Pictogram from './Pictogram';
  import Box from './Box';

  export default {
    name: 'TheNumbers',
    components: { Box, Pictogram },
  };
</script>

<style scoped lang="scss">
    @import "../assets/colors";
    @import "../assets/fonts";

    .numbers{
        background-color: $brand !important;
    }
    .numbers-container{
        display: grid;
        align-content: center;
        color: #ffffff;
        grid-row-gap: 2rem;
        @media all and (min-width: 1000px) {
            grid-template-columns: 2fr 1fr 1fr 1fr;
            grid-row-gap: 3rem;
        }

    }

    .info {
        grid-row: 1 / span 2;
        display: flex;
        flex-direction: column;
        justify-content: center;
        text-align: left;
        max-width: 450px;
    }

    .header {
        font-family: $font-bold;
        font-size: 3rem;
    }

    .subinfo {
        font-size: 1.75rem;
    }


</style>