<script setup lang="ts">
defineProps<{ size?: number }>()
</script>

<template>
  <svg
    :width="size ?? 34"
    :height="size ?? 34"
    viewBox="0 0 48 48"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    aria-hidden="true"
  >
    <defs>
      <linearGradient id="vs-grad" x1="8" y1="6" x2="40" y2="44" gradientUnits="userSpaceOnUse">
        <stop offset="0" stop-color="#3cb48a" />
        <stop offset="1" stop-color="#23835f" />
      </linearGradient>
    </defs>
    <rect x="4" y="4" width="40" height="40" rx="13" fill="url(#vs-grad)" />
    <path
      d="M24 34c-6.2-3.6-10-8.2-10-13 0-3.4 2.4-5.8 5.3-5.8 1.9 0 3.5 1.1 4.7 3 1.2-1.9 2.8-3 4.7-3 2.9 0 5.3 2.4 5.3 5.8 0 4.8-3.8 9.4-10 13Z"
      fill="#fff"
    />
    <path
      d="M29.3 20.2c.4-.8 1.2-1.4 2.1-1.4 1.5 0 2.6 1.3 2.6 3 0 2-1.6 3.9-3.6 5.6"
      stroke="#23835f"
      stroke-width="1.8"
      stroke-linecap="round"
      fill="none"
    />
    <path d="M24 14.2v6.4" stroke="#e08f3c" stroke-width="1.8" stroke-linecap="round" />
  </svg>
</template>