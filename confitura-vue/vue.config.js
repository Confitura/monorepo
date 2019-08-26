module.exports = {
  devServer: {
    proxy: {
      "^/api": {
        target: "https://2019.confitura.pl/",
        // target: 'http://localhost:3000',
        changeOrigin: true,
        logLevel: "debug"
      }
    }
  }
};
