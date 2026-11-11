const fs = require('fs');
const path = require('path');

function copyFolderSync(from, to) {
  if (!fs.existsSync(to)) {
    fs.mkdirSync(to, { recursive: true });
  }
  const entries = fs.readdirSync(from);
  for (const entry of entries) {
    const fromPath = path.join(from, entry);
    const toPath = path.join(to, entry);
    if (fs.lstatSync(fromPath).isDirectory()) {
      copyFolderSync(fromPath, toPath);
    } else {
      fs.copyFileSync(fromPath, toPath);
    }
  }
}

// 1. Sync public to dist
copyFolderSync('public', 'dist');

// 2. Copy root files for direct root serving
const rootFiles = [
  'index.html',
  'manifest.json',
  'sw.js',
  'dar_emblem.png',
  'darcorporate-release.apk',
  'darcorporate-ios.ipa',
  'darcorporate.mobileconfig',
  'manifest.plist'
];

for (const file of rootFiles) {
  const src = path.join('public', file);
  if (fs.existsSync(src)) {
    fs.copyFileSync(src, file);
  }
}

// Copy assets folder to root if needed
if (fs.existsSync('public/assets')) {
  copyFolderSync('public/assets', 'assets');
}

console.log('DARcorporate Vercel build complete: all files synced to root, public, and dist.');
