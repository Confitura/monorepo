module.exports = {
  devServer: {
    proxy: {
      '^/api': {
        target: 'https://2019.confitura.pl',
        changeOrigin: true,
      },
    },
  },
};
