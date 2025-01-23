const eslintPluginPrettier = require('eslint-plugin-prettier');
const eslintConfigPrettier = require('eslint-config-prettier');
const typescriptEslintPlugin = require('@typescript-eslint/eslint-plugin');
const typescriptEslintParser = require('@typescript-eslint/parser');

module.exports = [
    {
        files: ['src/**/*.{js,jsx,ts,tsx}'],
        ignores: ['node_modules'],
        parser: typescriptEslintParser,
        plugins: {
            prettier: eslintPluginPrettier,
            '@typescript-eslint': typescriptEslintPlugin,
        },
        rules: {
            ...eslintConfigPrettier.rules,
            'prettier/prettier': [
                'error',
                { singleQuote: true, semi: true, endOfLine: 'auto' },
            ],
        },
    },
];
