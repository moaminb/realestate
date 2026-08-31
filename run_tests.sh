#!/bin/bash
set -e

echo "🔨 در حال کامپایل پروژه و تست‌ها با جاوا ۸..."
mkdir -p bin
javac --release 8 -d bin $(find src test -name "*.java")

echo "🚀 در حال اجرای مجموعه تست‌های ماژولار..."
java -cp bin test.TestRunner
