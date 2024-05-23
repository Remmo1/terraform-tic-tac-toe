const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
    // Proxy requests to /result to localhost:8000
    app.use(
      '/result',
      createProxyMiddleware({
        target: 'http://localhost:8000',
        changeOrigin: true,
      })
    );
  
    // Proxy all other requests to localhost:8080
    app.use("/socket.io", (req, res, next) => {
        res.header("Access-Control-Allow-Origin", "*");
        next();
      });
    app.use(
        '/socket.io',
        createProxyMiddleware({
            target: 'http://localhost:8080',
            changeOrigin: false,
        })
    );

  };
