/** @type {import('tailwindcss').Config} */
module.exports = {
  corePlugins: {
    preflight: false,
  },
  prefix: 'tw-',
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    colors: {
      'primary': '#006699',
      'secondary': '#fbbc05',
      'bg_c': '#ffffff',
      'text_c': '#000000',
      'accent': '#28a745',
      'error': '#dc3545',
      'error_secondary': '#d95b68',
      'primary_light': '#5485AB',
      'primary_light_200': '#2DB3F5',
      'grey-200': '#D9D9D9',
      'grey-100': '#F1F1F1',
      'sky-100': '#e0f2fe',
      'fade_end': '#5485aa'
    },
    fontFamily: {
      'roboto': ['Roboto', 'sans-serif']
    },
    extend: {
      spacing: {
        '8xl': '96rem',
        '9xl': '128rem',
      },
      borderRadius: {
        '4xl': '2rem',
      }
    }
  },
}
