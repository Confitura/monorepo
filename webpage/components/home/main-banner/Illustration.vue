<template>
  <div class="illustration">

  </div>
</template>
<style scoped lang="scss">
@use "~/assets/media" as *;

.st2 {
  fill: none;
}

.st3 {
  fill: #aa0c2b;
}

.st5 {
  fill: #fff;
}

.st8 {
  fill: #adadad;
}

.st9 {
  fill: #939393;
}

.st10 {
  fill: none;
  stroke: #000;
  stroke-width: 2;
  stroke-miterlimit: 10;
}

.st13 {
  fill: #bcbcbc;
}

.st14,
.st15,
.st16 {
  stroke: #000;
  stroke-miterlimit: 10;
}

.st14 {
  stroke-width: 2;
  fill: #fff;
}

.st15,
.st16 {
  fill: none;
}

.st16 {
  fill: #939393;
  stroke-width: 2;
}


$levitate-speed: 3s;
$levitate-offset: -15px;

.illustration {
  width: 90vw;
  height: 90vw;
  position: relative;
  z-index: 1;

  @include md() {
    width: 55vw;
    height: 55vw;
    right: 0;
    position: absolute;
  }

  @include lg() {
    padding-top: 15vh;
    width: 50vw;
    height: 50vw;
    right: 0;
    position: absolute;
  }

  @include xxl() {
    width: 40vw;
    height: 40vw;
    max-width: 900px;
    max-height: 900px;
  }
}

.astronaut {
  filter: drop-shadow(10px 10px 0 rgba(0, 0, 0, 0.5));
  animation: levitate $levitate-speed alternate ease-in-out infinite;
}

@keyframes levitate {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(0, $levitate-offset);
  }
}
</style>
