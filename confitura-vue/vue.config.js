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
  },
  chainWebpack: config => {
    config.resolve.alias.set('vue', '@vue/compat')

    config.module
        .rule('vue')
        .use('vue-loader')
        .tap(options => {
          return {
            ...options,
            compilerOptions: {
              compatConfig: {
                MODE: 2
              }
            }
          }
        })
  }
};
