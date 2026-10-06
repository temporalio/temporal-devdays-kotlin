---
theme: ../
title: Example Workshop
info: |
  Minimal workshop deck demonstrating the `exercise` layout and
  configurable workshop TOC via `themeConfig.toc`.
themeConfig:
  footer: "Example Workshop | Temporal"
  toc:
    - id: arch
      label: Architecture
    - id: ex1
      label: "Exercise 1: Hello Workflow"
    - id: ex2
      label: "Exercise 2: Add an Activity"
    - id: agents
      label: AI Agents on Temporal
    - id: ex3
      label: "Exercise 3: Weather Agent"
    - id: ratelimit
      label: Rate Limit Demo
    - id: tooling
      label: Tooling and Metrics
    - id: wrap
      label: Wrap-Up
layout: cover
variant: planet-teal
---

# Example Workshop

## Building with Temporal

Workshop · Example event

---
layout: toc
current: arch
---

---
layout: section
---

# Architecture

---
layout: default
---

# Hello Workflow

Brief overview of what we'll build in Exercise 1: a Temporal Worker and a
first Workflow.

---
layout: exercise
minutes: 15
heading: Exercise 1
---

Start a local Temporal Service, register a Worker, and run a Hello Workflow
from the command line.

---
layout: toc
current: ex2
---

---
layout: exercise
minutes: 20
heading: Exercise 2
---

Add an Activity that calls an external service, and watch Temporal retry it
when the service fails.

---
layout: section
---

# AI Agents on Temporal

---
layout: exercise
minutes: 25
heading: Exercise 3
---

Build a weather agent that calls an LLM, fetches weather data from an
Activity, and writes the answer back through a Temporal Update.

---
layout: section
---

# Rate Limit Demo

---
layout: default
---

# Tooling and Metrics

Wrap-up of the day's tooling: the helper library and the metrics watcher we
deployed for observability.

---
layout: end
---

# Thank you

Workshop materials at **github.com/temporalio/example-workshop**
