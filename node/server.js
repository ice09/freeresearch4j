const express = require('express');
const vm = require('vm');

// Create the Express app
const app = express();
const port = 3000;

// Middleware to parse JSON request bodies
app.use(express.json());

// Global object to store kvStore instances keyed by chatId
const kvStores = {};

// POST /execute endpoint to receive code and run it
app.post('/execute', async (req, res) => {
    const code = req.body.code;
    // Get chatId from the request; if not provided, use 'default'
    const chatId = req.body.chatId || 'default';

    console.log(`\n=== New Execution Request ===`);
    console.log(`ChatId: ${chatId}`);
    console.log(`Received code:\n${code}`);

    if (!code) {
        console.error('Error: No code provided in the request.');
        return res.status(400).json({ error: 'No code provided. Expecting a JSON body with "code" field.' });
    }

    // Get or create the kvStore for this chatId
    if (!kvStores[chatId]) {
        kvStores[chatId] = {};
        console.log(`Created new kvStore for chatId: ${chatId}`);
    }
    const kvStore = kvStores[chatId];
    console.log(`Initial kvStore for chatId ${chatId}:`, kvStore);

    // Define a sandbox with useful libraries and the kvStore
    const sandbox = {
        Math: Math,
        Date: Date,
        console: console,
        http: require('http'),
        https: require('https'),
        setTimeout: setTimeout,
        clearTimeout: clearTimeout,
        setInterval: setInterval,
        kvStore: kvStore,
    };

    // Create a VM context with the sandbox
    const context = vm.createContext(sandbox);

    try {
        // Create and run the script
        const script = new vm.Script(code);
        let result = script.runInContext(context);
        console.log(`Execution result (pre-promise resolution) for chatId ${chatId}:`, result);

        // If the result is a Promise, await it
        if (result && typeof result.then === 'function') {
            result = await result;
            console.log(`Execution result (post-promise resolution) for chatId ${chatId}:`, result);
        }

        // Log the updated kvStore after execution
        console.log(`Updated kvStore for chatId ${chatId}:`, kvStore);

        // Return the result along with the chatId to indicate which kvStore was used
        res.json({ result, chatId });
    } catch (err) {
        // Log error details including stack trace for debugging
        console.error(`Error during execution for chatId ${chatId}:`, err);
        res.status(500).json({ error: err.toString(), stack: err.stack });
    }
});

// Start the server
app.listen(port, () => {
    console.log(`Server running at http://localhost:${port}`);
});
