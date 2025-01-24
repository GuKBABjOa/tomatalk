self.addEventListener('install', event => {
  console.log('Service worker installing...');
  // Perform install steps
});

self.addEventListener('activate', event => {
  console.log('Service worker activating...');
});

self.addEventListener('fetch', event => {
  console.log('Fetching:', event.request.url);
  // You can add custom fetch event handling here
});
