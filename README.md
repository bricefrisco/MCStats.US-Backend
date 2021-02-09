<p align="center">
	<img src="https://i.imgur.com/hprX8oH.png">
</p>

# MCStats : back-end

Realtime Minecraft server player counter. View both realtime and historical player counts of many of the most popular Minecraft servers. Heavily inspired by [Minetrack](https://github.com/Cryptkeeper/Minetrack 'Minetrack').

The ultimate goal is to count half a million Minecraft players during peak hours on over 1,000 Minecraft servers! I would like for as many servers as possible to be included. Currently, more popular servers (200+ players at peak hours) are prioritized to have the biggest impact.

### Features

- 📈 View player count history with pretty line charts!
- 👉 Select different timespans to see.
- 💽 Data is kept for two months, so you can see up-to two months history.
- 🕵️‍ Discover when servers are/are not at peak hours.

### Libraries and Frameworks: Back-end

- Written with Java and [Spring Boot](https://spring.io/projects/spring-boot 'Spring Boot').
- Data is stored in [MongoDB](https://www.mongodb.com/ 'MongoDB').
- Data is gathered by using [minecraft-server-ping](https://github.com/lucaazalim/minecraft-server-ping 'minecraft-server-ping').

### Libraries and Frameworks: Front-end

- MCStats front-end is written in [React](https://reactjs.org/ 'React').
- The charts are created with [ApexCharts](https://apexcharts.com/ 'ApexCharts').
- Pagination added using [react-paginate](https://github.com/AdeleD/react-paginate 'react-paginate').
- The searchable dropdown menu uses [react-select](https://react-select.com/home 'react-select').

Check out the [front-end](https://github.com/bricefrisco/mcstats-us-frontend") code as well!