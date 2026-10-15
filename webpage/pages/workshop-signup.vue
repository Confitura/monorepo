<template>
  <div class="workshop-signup">
    <PageHeader title="Workshop sign-up" type="coder"/>
    <Box class="content" color="white" :full="false">
      <div class="workshop-signup__info">
        <p>To register for a Confitura workshop, you need your conference ticket 😊</p>

        <ol class="workshop-signup__steps">
          <li>
            <p>The first step is to log in to your eBilet account using one of the available methods:</p>
            <img class="workshop-signup__image" src="/workshop-signup/image7.png"
                 alt="Logging in to your eBilet account"/>
          </li>
          <li>
            <p>Next, go to the Confitura event page and open the workshop you’re interested in:</p>
            <p>
              <a href="https://www.ebilet.pl/en/biznes/konferencje/confitura?partner=bcc"
                 target="_blank" rel="noopener">Confitura 26 | Konferencje | bilety na eBilet.pl</a>
            </p>
          </li>
          <li>
            <p>Select the number of tickets as <b>1</b>:</p>
            <img class="workshop-signup__image" src="/workshop-signup/image9.png"
                 alt="Selecting the number of tickets"/>
            <p>Then enter the <b>Access Code</b>, which is the ticket number located under the barcode:</p>
            <img class="workshop-signup__image" src="/workshop-signup/image8.jpg"
                 alt="Entering the Access Code"/>
          </li>
          <li>
            <p>Click <b>“Add to basket”</b> and proceed through the checkout process by
              selecting <b>“Buy 1 Ticket”</b>:</p>
            <img class="workshop-signup__image" src="/workshop-signup/image11.png"
                 alt="Add to basket / Buy 1 Ticket"/>
          </li>
          <li>
            <p>Click <b>“Next”</b>:</p>
            <img class="workshop-signup__image" src="/workshop-signup/image10.png"
                 alt="Next step"/>
          </li>
          <li>
            <p>Provide your contact details and click <b>“Next”</b>:</p>
            <img class="workshop-signup__image" src="/workshop-signup/image2.png"
                 alt="Contact details"/>
          </li>
          <li>
            <p>Enter the ticket holder’s information and click <b>“Next”</b>:</p>
            <img class="workshop-signup__image" src="/workshop-signup/image1.png"
                 alt="Ticket holder information"/>
          </li>
          <li>
            <p>Choose <b>“Electronic ticket”</b> and click <b>“Next”</b>:</p>
            <img class="workshop-signup__image" src="/workshop-signup/image4.png"
                 alt="Electronic ticket"/>
          </li>
          <li>
            <p>Select <b>“Online payment”</b> — since the cost is 0, no actual payment
              will occur — and click <b>“Next”</b>:</p>
            <img class="workshop-signup__image" src="/workshop-signup/image3.png"
                 alt="Online payment"/>
          </li>
          <li>
            <p>Accept the terms and conditions and click <b>“I confirm order and pay”</b>:</p>
            <img class="workshop-signup__image" src="/workshop-signup/image6.png"
                 alt="Confirming the order"/>
          </li>
        </ol>

        <p>The ticket should arrive at the email address associated with your logged-in
          account. You can also find it in your user panel:</p>
        <img class="workshop-signup__image" src="/workshop-signup/image5.png"
             alt="Ticket in the user panel"/>
      </div>
    </Box>
    <Contact/>
  </div>
</template>

<script setup lang="ts">
const title = 'Workshop sign-up — Confitura 2026';
const description = 'Step-by-step guide to registering for a Confitura 2026 workshop via eBilet.';
useHead({
  title,
  meta: [
    {name: 'description', content: description},
    {property: 'og:title', content: title},
    {property: 'og:description', content: description},
    {property: 'og:type', content: 'website'},
    {name: 'twitter:card', content: 'summary'},
    {name: 'twitter:title', content: title},
    {name: 'twitter:description', content: description}
  ]
})
</script>

<style lang="scss" scoped>
@use "~/assets/colors" as *;
@use "~/assets/sizes" as *;
@use "~/assets/media" as *;

.workshop-signup__info {
  max-width: 720px;
  margin: 0 auto;
  font-size: 1.3rem;
  line-height: 1.9rem;

  p {
    margin: 1.2rem 0;
  }

  a {
    color: $brand;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}

.workshop-signup__steps {
  margin: 1.5rem 0;
  padding-left: 2.5rem;

  li {
    margin-bottom: 2rem;

    &::marker {
      color: $brand;
      font-weight: bold;
    }

    p:first-child {
      margin-top: 0;
    }
  }
}

.workshop-signup__image {
  display: block;
  max-width: 100%;
  height: auto;
  margin: 1rem 0 2rem;
  border: 1px solid #dee2e6;
  border-radius: 4px;
}
</style>
