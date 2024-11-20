const { generateService } = require("@umijs/openapi");

generateService({
    requestLibPath: "import request from '@/lib/request'",
    schemaPath: "http://localhost:9998/api/v3/api-docs",
    serversPath: "./src",
});
