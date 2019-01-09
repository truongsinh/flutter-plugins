#!/usr/bin/env node

//https://codecov.io/gh/truongsinh/flutter-plugins/tree/master/packages/cloud_firestore

const dest = __dirname + "/../packages/";
const { readdir, writeFile } = require("fs");
const { promisify } = require("util");
const [readDirAsync, writeFileAsync] = [
  promisify(readdir),
  promisify(writeFile)
];
const rp = require("request-promise-native");
const { JSDOM } = require("jsdom");

class Lazy {
  constructor(iterable, callback) {
    this.iterable = iterable;
    this.callback = callback;
  }

  filter(callback) {
    return new LazyFilter(this, callback);
  }

  map(callback) {
    return new LazyMap(this, callback);
  }

  next() {
    return this.iterable.next();
  }

  take(n) {
    const values = [];
    for (let i = 0; i < n; i++) {
      values.push(this.next().value);
    }

    return values;
  }
}

class LazyFilter extends Lazy {
  next() {
    while (true) {
      const item = this.iterable.next();

      if (this.callback(item.value)) {
        return item;
      }
    }
  }
}

class LazyMap extends Lazy {
  next() {
    const item = this.iterable.next();

    const mappedValue = this.callback(item.value);
    return { value: mappedValue, done: item.done };
  }
}

const main = async () => {
  const dirList = await readDirAsync(dest);
  const dirListGenerator = new Lazy(
    (function*() {
      for (let i = 0; i < dirList.length; i++) {
        yield dirList[i];
      }
    })()
  );
  // dirList
  dirListGenerator
    .map(async dirName => {
      if (!dirName) {
        return;
      }
      try {
        const codeCovUrl = `https://codecov.io/gh/truongsinh/flutter-plugins/tree/master/packages/${dirName}`;
        const codeCovHtmlResponse = await rp(codeCovUrl);
        const document = new JSDOM(codeCovHtmlResponse).window.document;
        const jsDomNodeList = document.querySelectorAll("#tree > tbody > tr");
        const length = jsDomNodeList.length;

                let color = "red";
        let coverage = 0;
        if (length > 0) {
          if (length === 1) {
            coverage = parseInt(
              document
                .querySelector("#tree > tbody > tr > td:nth-last-child(1)")
                .innerHTML.trim()
                .slice(0, -1)
            );
          } else {
            coverage = parseInt(
              document
                .querySelector("#tree > tfoot > tr > th:nth-last-child(1)")
                .innerHTML.trim()
                .slice(0, -1)
            );
            switch (length) {
              case 2:
                color = "orange";
                break;
              default:
                color = [
                  // 'gray' // missing all 3 platform (dart, ios, android)
                  // 'red' // missing 2 out of 3 platforms
                  // 'orange', // missing 1 out of 3 platforms
                  "yellow", // 0-25
                  "yellowgreen", // 25-50
                  "green", // 50-75
                  "brightgreen", // 75-100
                  "brightgreen" // 100
                ][Math.floor(coverage / 25)];
                break;
            }
          }
        }
        return {
          schemaVersion: 1,
          label: dirName,
          message: coverage + "%",
          color
        };
      } catch (e) {
        console.log(e);
      }
    })
    .map(async objPromise => {
      const obj = await objPromise;
      console.log(obj);
      if (!obj) {
        return;
      }
      await writeFileAsync(
        __dirname + "/" + obj.label + ".json",
        JSON.stringify(obj, null, 2)
      );
    })
    .take(40);
};

main();
