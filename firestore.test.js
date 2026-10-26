const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- SECURITY BOUNDS TESTS ---

test("Unauthenticated user: cannot read users profile", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: can read profiles", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).set({
      username: "alice",
      fullName: "Alice Smith",
      avatarUrl: "https://example.com/avatar.jpg",
      createdAt: new Date(),
      updatedAt: new Date()
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertSucceeds(bobDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: cannot update someone else's profile", async () => {
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).set({
    username: "alice_hacked",
    fullName: "Hacked",
    avatarUrl: "https://example.com/avatar.jpg",
    createdAt: new Date(),
    updatedAt: new Date()
  }));
});

test("Authenticated user: can create their own post", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("posts").doc("post_1").set({
    userId: ALICE_UID,
    authorUsername: "alice",
    authorAvatarUrl: "https://example.com/avatar.jpg",
    mediaUrl: "https://example.com/post.jpg",
    caption: "Hello world!",
    createdAt: new Date(),
    updatedAt: new Date()
  }));
});

test("Authenticated user: cannot create post with spoofed userId", async () => {
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("posts").doc("post_fake").set({
    userId: ALICE_UID, // Spoofed!
    authorUsername: "alice",
    authorAvatarUrl: "https://example.com/avatar.jpg",
    mediaUrl: "https://example.com/post.jpg",
    caption: "Spoofed!",
    createdAt: new Date(),
    updatedAt: new Date()
  }));
});
