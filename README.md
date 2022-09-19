# Vagrant Repository
This is a Vagrant Repository for private hosting of Vagrant boxes

Find the code in the GitHub repository:

* [https://github.com/acntech/vagrant-repository](https://github.com/acntech/vagrant-repository)

### API
The API contains a set of REST endpoints for managing hosted Vagrant Boxes in the repository.
This means read and write operations for organizations, boxes, versions and providers for the repository.

It also provides the endpoints used by Vagrant clients to retrieve information about hosted Vagrant boxes.

### GUI
The GUI is the web interface that is used to manage hosted Vagrant Boxes in the repository.

## Getting started
The Vagrant Repository are available as a Docker container in the Docker Hub:

* [vagrant-repository](https://hub.docker.com/r/acntechie/vagrant-repository)

### Runtime
Use the following ```docker-compose.yml``` file to run the Vagrant Repository:

```yaml
version: '3.7'

services:
  gui:
    image: acntechie/vagrant-repository
    container_name: vagrant-repository
    ports:
      - "8080:8080"
```

### Hosting boxes
Vagrant boxes belong to an organization. A box can exist as one or more version.
A box version is published with an associated provider.
The provider is the virtualization platform that the box is meant to run on.
As of now VirtualBox is the only supported provider.

To use a hosted box like e.g. ```acntech/ubuntu``` in Vagrant then define a ```Vagrantfile``` similar to the example below:

```ruby
# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure(2) do |config|

  # Name of Vagrant Box
  config.vm.box = "acntech/ubuntu"

  # URL to Vagrant Repository for given Vagrant Box
  config.vm.box_url = "http://repo.private.com/api/box/acntech/ubuntu"

  # Don't generate new SSH key, but use the default insecure key
  config.ssh.insert_key = false

  config.vm.provider "virtualbox" do |vb|

    # Name to display in VirtualBox
    vb.name = "AcnTech Ubuntu"

    # Display the VirtualBox GUI when booting the machine
    vb.gui = true

    # Customize the amount of memory on the VM
    vb.memory = "1024"

    # Customize CPU count
    vb.cpus = "1"
  end
end
```
