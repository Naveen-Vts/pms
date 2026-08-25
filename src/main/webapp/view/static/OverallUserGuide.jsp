<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"
	import="java.util.*,com.vts.*" %>
	<%@ page import="java.time.LocalDate" %>
		<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
			<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

				<!DOCTYPE html>
				<html>

				<head>
					<meta charset="UTF-8">
					<title>Overall User Guide</title>
					<% String loginPage=(String) session.getAttribute("loginPage"); %>
						<meta charset="ISO-8859-1">
						<meta name="viewport" content="width=device-width, initial-scale=1.0" />
						<title>
							<%=loginPage.equalsIgnoreCase("login") ? "PMS" : "WR" %>
						</title>

						<link rel="shortcut icon" type="image/png" href="view/images/drdologo.png">

						<spring:url value="/resources/css/dashboard.css" var="dashboardCss" />
						<link href="${dashboardCss}" rel="stylesheet" />

						<spring:url value="/resources/css/newfont.css" var="NewFontCss" />
						<link href="${NewFontCss}" rel="stylesheet" />

						<spring:url value="/resources/css/master.css" var="masterCss" />
						<link href="${masterCss}" rel="stylesheet" />

						<jsp:include page="../static/dependancy.jsp"></jsp:include>
						<style>
							.main-wrapper {
								display: flex;
								min-height: 100vh;
							}

							/* Sticky sidebar */
							.sidebar {
								width: 200px;
								background: linear-gradient(to right, #021B79);
								color: #ffffff;
								padding: 20px 10px;
								display: flex;
								flex-direction: column;
								position: sticky;
								top: 0;
								height: 100vh;
							}

							.sidebar.expanded {
								width: 65px;
							}

							.toggle-btn {
								background: #cde3e2;
								border: 1px solid #cde3e2;
								cursor: pointer;
								font-size: 20px;
								margin-bottom: 20px;
							}

							.menu-container {
								flex: 1;
								overflow-y: auto;
								overflow-x: hidden;
								scrollbar-width: thin;
								scrollbar-color: #0d6efd #0d6efd;
								max-height: calc(100vh - 60px);
							}

							.menu-container::-webkit-scrollbar {
								width: 8px;
							}

							.menu-container::-webkit-scrollbar-track {
								background: #0d6efd;
								border-radius: 10px;
							}

							.menu-container::-webkit-scrollbar-thumb {
								background-color: #0d6efd;
								border-radius: 10px;
								border: 2px solid #0d6efd;
							}

							.menu-container::-webkit-scrollbar-thumb:hover {
								background-color: #0d6efd;
							}

							.menu {
								display: flex;
								flex-direction: column;
								gap: 10px;
							}

							.menu-item {
								display: flex;
								align-items: center;
								gap: 10px;
								padding: 10px;
								border-radius: 10px;
								cursor: pointer;
								font-size: 18px;
								color: #ffffff;
								transition: background 0.2s;
							}

							.menu-item:hover {
								background-color: #f0f9f8;
								color: #0d6efd;
							}

							.menu-item.active {
								background-color: #e6f4f2;
								color: #0d6efd;
								font-weight: 500;
							}

							/* Content styling */
							.main-content {
								flex: 1;
								padding: 30px;
								overflow-y: auto;
							}

							.content-section {
								display: none;
							}

							.content-section.active {
								display: block;
							}

							/* Responsive tweaks */
							@media (max-width: 768px) {
								.main-wrapper {
									flex-direction: column;
								}

								.sidebar {
									width: 100%;
									border-radius: 0;
									height: auto;
									position: relative;
								}

								.sidebar.collapsed {
									width: 100% !important;
									padding: 10px !important;
									align-items: flex-start;
								}

								.sidebar.collapsed .menu-item {
									justify-content: flex-start;
									font-size: 18px;
								}

								.sidebar.collapsed .menu-item span {
									display: inline;
								}
							}

							.toggle-btn:focus {
								outline: none;
								box-shadow: none;
							}

							.card-heading {
								display: flex;
								justify-content: space-between;
								align-items: center;
								user-select: none;
								font-size: 18px;
							}

							.list-group-numbered {
								counter-reset: section;
								padding-left: 0;
								list-style: none;
							}

							.list-group-numbered>.list-group-item::before {
								counter-increment: section;
								content: counter(section) ". ";
								font-weight: bold;
								margin-right: 10px;
							}

							.card-hover {
								background: white !important;
							}

							.card-hover:hover {
								color: white !important;
								background: linear-gradient(to right, #106bce, #0ffcbe) !important;
								transition: width 0.4s ease-in-out;
							}

							a:hover {
								color: #333;
								text-decoration: none;
							}

							html {
								scroll-behavior: smooth;
							}

							.scrollable {
								height: 250px;
								/* or any height you want */
								overflow-y: auto;
								/* scrolls vertically */
							}

							/* COLLAPSED sidebar */
							.sidebar.collapsed {
								width: 70px !important;
								padding: 20px 5px !important;
								align-items: center;
							}

							.sidebar.collapsed .menu-item {
								justify-content: center;
								font-size: 20px;
							}

							.sidebar.collapsed .menu-item span:not(.tooltip-icon) {
								display: none;
							}

							.sidebar .sidebar-title {
								transition: opacity 0.3s ease;
								white-space: nowrap;
								overflow: hidden;
								text-overflow: ellipsis;
							}

							/* Hide title when collapsed */
							.sidebar.collapsed .sidebar-title {
								display: none;
							}

							.sidebar-header {
								display: flex;
								align-items: center;
								justify-content: space-between;
							}



							.sidebar .menu-item i {
								display: none;
							}

							.sidebar.collapsed .menu-item i {
								display: block;
							}

							.tooltip {
								pointer-events: none;
							}

							.tooltip-inner {
								background-color: #0d6efd !important;
								color: white !important;
								font-weight: bold;
							}

							.bs-tooltip-right .arrow {
								border-right: #0d6efd !important;
							}

							#topBtn {
								position: fixed;
								bottom: 30px;
								right: 30px;
								z-index: 999;
								background-color: #28a745;
								color: white;
								border: none;
								padding: 10px 15px;
								border-radius: 50%;
								font-size: 16px;
								cursor: pointer;
								display: none;
								box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
							}

							#topBtn:hover {
								background-color: #1d7b34;
							}
						</style>

				</head>

				<body>
					<div class="main-wrapper">
						<!-- Starting line of Sidebar Content -->
						<div class="sidebar" id="sidebar"> <!-- 'collapsed' class controls the state -->
							<div class="d-flex gap-2 align-items-center sidebar-header">
								<h5 class="sidebar-title">PMS Guide</h5>
								<button class="toggle-btn" onclick="toggleSidebar()"><i class="fa fa-bars"></i></button>
							</div>
							<div class="menu-container mt-3">
								<div class="menu">
									<a href="#" class="menu-item active" data-target="ProjectManagement"
										data-toggle="tooltip" data-placement="right" title="Project">
										<i class="fa fa-play"></i>
										<span class="menu-text">Project</span>
									</a>
									<a href="#" class="menu-item" data-target="PreProjectInitiation"
										data-toggle="tooltip" data-placement="right" title="Pre-Project">
										<i class="fa fa-hourglass-start"></i>
										<span class="menu-text">Pre-Project</span>
									</a>
									<a href="#" class="menu-item" data-target="Committee" data-toggle="tooltip"
										data-placement="right" title="Committee">
										<i class="fa fa-users"></i>
										<span class="menu-text">Committee</span>
									</a>
									<a href="#" class="menu-item" data-target="Schedule" data-toggle="tooltip"
										data-placement="right" title="Schedule">
										<i class="fa fa-calendar"></i>
										<span class="menu-text">Schedule</span>
									</a>
									<a href="#" class="menu-item" data-target="Action" data-toggle="tooltip"
										data-placement="right" title="Action">
										<i class="fa fa-tasks"></i>
										<span class="menu-text">Action</span>
									</a>
									<a href="#" class="menu-item" data-target="Milestone" data-toggle="tooltip"
										data-placement="right" title="Milestone">
										<i class="fa fa-flag-checkered"></i>
										<span class="menu-text">Milestone</span>
									</a>
									<a href="#" class="menu-item" data-target="Procurement" data-toggle="tooltip"
										data-placement="right" title="Procurement">
										<i class="fa fa-shopping-cart"></i>
										<span class="menu-text">Procurement</span>
									</a>
									<a href="#" class="menu-item" data-target="CARS" data-toggle="tooltip"
										data-placement="right" title="CARS">
										<i class="fa fa-book"></i>
										<span class="menu-text">CARS</span>
									</a>
									<a href="#" class="menu-item" data-target="WorkRegister" data-toggle="tooltip"
										data-placement="right" title="Work Register">
										<i class="fa fa-list-alt"></i>
										<span class="menu-text">Work Register</span>
									</a>
								</div>
							</div>
						</div>
						<!-- Ending line of Sidebar Content -->
						<!-- Main content -->
						<div class="main-content">
							<!-- Starting line of Project Content -->
							<div id="ProjectManagement" class="content-section active">
								<div class="card border-success">
									<div class="card-header bg-success text-white">
										<h5 class="ml-3">
											<i class="fa fa-play"></i> Project Management
										</h5>
									</div>
									<div class="card-body">
										<div class="alert alert-info" role="alert">
											<i class="fa fa-info-circle me-2"></i>
											All projects must be entered in the <strong>Project
												Main</strong> section.
											For any Sub-Project, you must also enter the <strong>Main Project</strong>
											details to which it belongs.
										</div>
										<div class="alert alert-info" role="alert">
											<i class="fa fa-info-circle me-2"></i>
											Only users with the roles of Admin or Project Director are authorized to
											create new projects.
										</div>

										<!-- Project Main -->
										<div class="card border-primary mb-4 ">
											<div class="card-header card-heading text-primary card-hover"
												data-toggle="collapse" data-target="#project-main" aria-expanded="true"
												aria-controls="project-main">
												<span><i class="fa fa-clipboard me-2"></i> Project Main </span> <i
													class="fa fa-chevron-down"></i>
											</div>
											<div id="project-main" class="collapse show">
												<div class="card-body">
													<strong class="text-primary">To Add a project </strong>
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click <strong
																class="text-success">Master <i
																	class="fa fa-chevron-right"></i> Project Main <i
																	class="fa fa-chevron-right"></i> Add
															</strong>.
														</li>
														<li class="list-group-item"><strong>Fill the
																details: <em> Project Code, Project Name, Project
																	Number, Project Unit Code, End User, Project Short
																	Name ,
																	Category, Security Classification, Project Director,
																	Is Main
																	Work Center, Project Sanc Authority , Project
																	Sanction
																	Letter No, Project Sanction Date, Total Sanction
																	Cost, Board
																	Of Reference, Sanction Cost FE , PDC, Sanction Cost
																	RE ,
																	Platform, Nodal & Participating Lab, Application,
																	Scope,
																	Objective and Deliverable. </em>
															</strong> <i class="fa fa-chevron-right"></i> Click <strong
																class="text-success">Submit</strong>.</li>
													</ul>
													<strong class="text-primary">To Edit a Project</strong>
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click <strong
																class="text-success">Master <i
																	class="fa fa-chevron-right"></i> Project Main <i
																	class="fa fa-chevron-right"></i> Select a Project <i
																	class="fa fa-chevron-right"></i> Edit.
															</strong>.
														</li>
														<li class="list-group-item"><strong>Edit the
																details</strong> <i class="fa fa-chevron-right"></i>
															Click <strong class="text-success">Submit</strong>.</li>
													</ul>
												</div>
											</div>
										</div>
										<!-- Project Master -->
										<div class="card border-info mb-4">
											<div class="card-header card-heading card-hover info text-info"
												data-toggle="collapse" data-target="#project-master"
												aria-expanded="false" aria-controls="project-master">
												<span><i class="fa fa-clipboard me-2"></i> Project Master
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div id="project-master" class="collapse">
												<div class="card-body">
													<strong class="text-primary">To Add a project </strong>
													<div class="alert alert-info mt-2" role="alert">
														<i class="fa fa-info-circle me-2"></i>
														For a <strong>Main Project</strong>, select
														<strong>Yes</strong>. For a <strong>Sub-Project</strong>, select
														<strong>No</strong> and then choose the corresponding Main
														Project.
													</div>

													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click <strong
																class="text-success">Master <i
																	class="fa fa-chevron-right"></i> Project Master <i
																	class="fa fa-chevron-right"></i> Add
															</strong>.
														</li>
														<li class="list-group-item"><strong>Fill the
																details: <em> Project Main, Project Name, Project
																	Number, Project Unit Code, Project Director, Project
																	Short
																	Name, End User, Project Code, Project Sanc
																	Authority,
																	Project Sanction Letter No, Project Sanction Letter
																	No,
																	Project Sanction Date, Total Sanction Cost, Board Of
																	Reference, Sanction Cost FE , PDC, Sanction Cost RE
																	,
																	Platform, Nodal & Participating Lab, Application,
																	Scope,
																	Objective and Deliverable. </em>
															</strong> <i class="fa fa-chevron-right"></i> Click <strong
																class="text-success">Submit</strong>.</li>
													</ul>
													<strong class="text-primary">To Edit a Project</strong>
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click <strong
																class="text-success">Master <i
																	class="fa fa-chevron-right"></i> Project Master <i
																	class="fa fa-chevron-right"></i> Select a Project <i
																	class="fa fa-chevron-right"></i> Edit.
															</strong>.
														</li>
														<li class="list-group-item"><strong>Edit the
																details</strong> <i class="fa fa-chevron-right"></i>
															Click <strong class="text-success">Submit</strong>.</li>
													</ul>
													<strong class="text-primary">To Revise a Project</strong>
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click <strong
																class="text-success">Master <i
																	class="fa fa-chevron-right"></i> Project Master <i
																	class="fa fa-chevron-right"></i> Select a Project <i
																	class="fa fa-chevron-right"></i> Revise.
															</strong>.
														</li>
														<li class="list-group-item"><strong>Revise the
																details <i class="fa fa-chevron-right"></i> Enter Remark
															</strong> <i class="fa fa-chevron-right"></i> Click <strong
																class="text-success">Revise</strong>.

															<h6 class="alert alert-info mt-2" role="alert">
																<i class="fa fa-info-circle"></i>
																<strong>Note:</strong> Once a project is revised, you
																<strong>cannot edit</strong> that project.
															</h6>


														</li>
														</h6>
													</ul>
													<strong class="text-primary">To add Attachment to a
														project</strong>
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click <strong
																class="text-success">Master <i
																	class="fa fa-chevron-right"></i> Project Master <i
																	class="fa fa-chevron-right"></i> Select a Project <i
																	class="fa fa-chevron-right"></i> Attachments.
															</strong>.
														</li>
														<li class="list-group-item">On the Right hand side, Enter file
															name and upload files <i class="fa fa-chevron-right"></i>
															Click <strong class="text-success">Submit</strong>.
															<div class="alert alert-info mt-2" role="alert">
																<i class="fa fa-info-circle me-2"></i>
																You can upload multiple files by clicking on <i
																	class="fa fa-plus"></i> icon.
															</div>
														</li>
														<li class="list-group-item">On the Left hand side, you can
															download or delete that particular file by clicking on
															<i class="fa fa-download text-success"></i> or <i
																class="fa fa-trash text-danger"></i> buttons.
														</li>


													</ul>
													<strong class="text-primary">To View a Project</strong>
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click <strong
																class="text-success">Master <i
																	class="fa fa-chevron-right"></i> Project Master <i
																	class="fa fa-chevron-right"></i> Select a Project <i
																	class="fa fa-chevron-right"></i> View.
															</strong>.
														</li>
													</ul>
												</div>
											</div>
										</div>
										<!-- Project Team Creation -->
										<div class="card border-success mb-4 ">
											<div class="card-header card-heading card-hover text-success"
												data-toggle="collapse" data-target="#project-team-correction"
												aria-expanded="false" aria-controls="project-team-correction">
												<span><i class="fa fa-users me-2"></i> Project Team </span> <i
													class="fa fa-chevron-down"></i>
											</div>
											<div id="project-team-correction" class="collapse">
												<div class="card-body">
													<ul class="list-group list-group-numbered">

														<li class="list-group-item">Click <strong
																class="text-success">Master <i
																	class="fa fa-chevron-right"></i> Project Team

															</strong>.
														</li>

														<li class="list-group-item">Select project at the top <i
																class="fa fa-chevron-right"></i> Select employees from
															right-hand side <i class="fa fa-chevron-right"></i> Click
															<strong class="text-success">Assign</strong>.
															<div class="alert alert-info mt-2" role="alert">
																<i class="fa fa-info-circle me-2"></i>
																You can select multiple employees at once.
															</div>

														</li>
														<h6 class="text-primary">To delete Team Members</h6>
														<li class="list-group-item"> Select the employees from the
															left-hand side <i class="fa fa-chevron-right"></i> Click
															<strong class="text-danger">Remove</strong>.
														</li>
													</ul>
												</div>
											</div>
										</div>
										<!-- Project Data -->
										<div class="card border-danger mb-4">
											<div class="card-header card-heading card-hover text-danger"
												data-toggle="collapse" data-target="#project-data" aria-expanded="false"
												aria-controls="project-data">
												<span><i class="fa fa-info-circle me-2"></i> Project Data
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div id="project-data" class="collapse">
												<div class="card-body">
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click <strong
																class="text-success">Master <i
																	class="fa fa-chevron-right"></i> Project Data
															</strong>.
														</li>
														<li class="list-group-item">Select Project at
															top-right-corner <i class="fa fa-chevron-right"></i> Fill
															the
															details <i class="fa fa-chevron-right"></i> Click <strong
																class="text-success">Submit/Edit.</strong>
														</li>
														<h6 class="text-primary">To Revise</h6>
														<li class="list-group-item">Revise the details <i
																class="fa fa-chevron-right"></i> Click <strong
																class="text-primary">Revise</strong>.
														</li>
													</ul>
													<div class="alert alert-info mt-2" role="alert">
														<i class="fa fa-info-circle me-2"></i>
														The project stage and procurement limit set in the
														<strong>Project Data</strong> section will be displayed in the
														<strong>Briefing Paper</strong>.
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<!-- Ending line of Project Content -->
							<!-- Starting line of Pre-Project Content -->
							<div id="PreProjectInitiation" class="content-section">
								<div class="card border-success ">
									<div class="card-header bg-success text-white">
										<h5 class="ml-3">
											<i class="fa fa-hourglass-start"></i> Pre-Project
										</h5>
									</div>
									<div class="card-body">
										<div class="alert alert-info" role="alert">
											<i class="fa fa-info-circle me-2"></i>
											The Initiation will be done by the Head P&C, based on the Director's
											instructions. The selected Project Director Designate will then enter the
											remaining project details.
										</div>

										<!-- Initiation -->
										<div class="card border-success mb-4">
											<div class="card-header card-heading card-hover text-success"
												data-toggle="collapse" data-target="#initiate-pre-project"
												aria-expanded="true" aria-controls="initiate-pre-project">
												<span> <i class="fa fa-hourglass-start me-2"></i>
													Pre-Project Initiation
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div class="collapse show" id="initiate-pre-project">
												<div class="card-body">
													<ul class="list-group list-group-numbered">

														<li class="list-group-item">
															<div class="alert alert-info mt-2" role="alert">
																<i class="fa fa-info-circle me-2"></i>
																For a <strong>Main Project</strong>, select
																<strong>Yes</strong>. For a
																<strong>Sub-Project</strong>, select
																<strong>No</strong> and then choose the corresponding
																Main
																Project.
															</div> Click <strong class="text-primary">RoadMap <i
																	class="fa fa-chevron-right"></i> Initiate <i
																	class="fa fa-chevron-right"></i> Choose Project
																(Main/Sub)
															</strong>.
														</li>
														<li class="list-group-item">Fill the details: <em>
																Project/Programme, Category, Security Classification,
																Nodal
																Lab, Short Name, Project Title , Is Planned,
																Deliverable,
																Project Director Designate (PDD), Indicative
																Duration(Mos), Indicative Cost(Rs), P&C
																DO
																Remarks, Probable Start Date. </em> <i
																class="fa fa-chevron-right"></i>
															Click <strong class="text-success">Submit</strong>.

														</li>

														<li class="list-group-item">
															<h6 class="text-primary">To Edit the Project Details</h6>
															&nbsp;
															Click <strong class="text-success">RoadMap <i
																	class="fa fa-chevron-right"></i> Initiation List <i
																	class="fa fa-chevron-right"></i> Click on <i
																	class="fa fa-file-text text-primary"></i> Details of
																any
																project <i class="fa fa-chevron-right"></i> <strong
																	class="text-dark">Edit the Details</strong> <i
																	class="fa fa-chevron-right"></i> <strong
																	class="text-dark">Click
																</strong>Submit.
															</strong>
													</ul>
												</div>
											</div>
										</div>
										<!-- Reference -->
										<div class="card border-primary mb-4">
											<div class="card-header card-heading card-hover text-primary"
												data-toggle="collapse" data-target="#Reference-project"
												aria-expanded="false" aria-controls="Reference-project">
												<span> <i class="fa fa-link me-2"></i> Reference
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div class="collapse" id="Reference-project">
												<div class="card-body">
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click
															<strong class="text-success">Roadmap <i
																	class="fa fa-chevron-right"></i>
																Initiation List <i class="fa fa-chevron-right"></i>
																Click <i class="fa fa-file-text text-primary"></i>
																Details of any
																project <i class="fa fa-chevron-right"></i> Reference
															</strong>.
														</li>

														<li class="list-group-item">Fill the details: <em>
																Reference Authority, Reference Number, Reference Date,
																Reference Attachment </em> <i
																class="fa fa-chevron-right"></i>
															Click <strong class="text-success">Submit.</strong>
															<h6 class="text-primary" style="text-indent: 20px;">
																You can edit the reference by clicking on <strong
																	style="color: darkorange;">Edit</strong> button.
															</h6>
														</li>
													</ul>
												</div>
											</div>
										</div>
										<!-- Details -->
										<div class="card border-danger mb-4">
											<div class="card-header card-heading card-hover text-danger"
												data-toggle="collapse" data-target="#Details-project"
												aria-expanded="false" aria-controls="Details-project">
												<span> <i class="fa fa-file-text me-2"></i> Details
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div class="collapse" id="Details-project">
												<div class="card-body">
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click
															<strong class="text-success">Roadmap <i
																	class="fa fa-chevron-right"></i>
																Initiation List <i class="fa fa-chevron-right"></i>
																Click <i class="fa fa-file-text text-primary"></i>
																Details of any
																project <i class="fa fa-chevron-right"></i> Details
															</strong>.
														</li>

														<li class="list-group-item">Fill the details: <em>
																Describe the <strong class="text-success">Need for
																	the project, Requirement, World Scenario, Objective,
																	Scope,
																	Multi-Lab Work Share, Earlier Work, Competency
																	Established,
																	Technology Challenges, Risk Mitigation, Proposal and
																	Realization Plan</strong> with a brief summary and a
																detailed
																explanation.

															</em> <i class="fa fa-chevron-right"></i> Click <strong
																class="text-success">Submit after entering each
																fields.</strong>
															<h6 class="text-primary font-weight-bold"
																style="margin-left: 20px;">
																You can edit the details by clicking on <strong
																	style="color: darkorange;"><i
																		class="fa fa-edit"></i></strong>
																button.
															</h6>
														</li>
													</ul>
												</div>
											</div>
										</div>
										<!-- COST -->
										<div class="card border-dark mb-4">
											<div class="card-header card-heading card-hover text-dark"
												data-toggle="collapse" data-target="#Cost-project" aria-expanded="false"
												aria-controls="Cost-project">
												<span> <i class="fa fa-usd me-2"></i> Cost
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div class="collapse" id="Cost-project">
												<div class="card-body">
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click <strong
																class="text-success">Roadmap <i
																	class="fa fa-chevron-right"></i>
																Initiation List <i class="fa fa-chevron-right"></i>
																Click <i class="fa fa-file-text text-primary"></i>
																Details of any
																project <i class="fa fa-chevron-right"></i> Cost
															</strong>.
														</li>

														<li class="list-group-item">Fill the details: <em>
																Budget Head, Item, Item Detail and Cost </em> <i
																class="fa fa-chevron-right"></i> Click <strong
																class="text-success">Submit.</strong>
														</li>
														<h6 class="text-primary">To edit/delete cost items</h6>
														<li class="list-group-item">Scroll down to the item list.
															Click <i class="fa fa-edit text-success"></i>/ <i
																class="fa fa-trash text-danger"></i> button.
														</li>
														<h6 class="text-primary">To add procurement plan for cost
															item</h6>
														<li class="list-group-item">Click <strong
																class="text-success">View
																Button</strong> <i class="fa fa-chevron-right"></i>
															Click <strong class="text-success">Add</strong> <i
																class="fa fa-chevron-right"></i>
															Fill the details: <em>
																Item, Purpose, Source, Mode, Cost, EPC Approval
																required(Yes/No), Demand, Tender, Order and Payment
															</em> <i class="fa fa-chevron-right"></i> Click <strong
																class="text-success">
																Submit. </strong>
															<h6 class="text-primary">
																To edit the procurement plan. Click on
																<strong>Edit</strong>
																button.
															</h6>
														</li>

													</ul>
												</div>
											</div>
										</div>
										<!-- Schedule -->
										<div class="card border-info mb-4">
											<div class="card-header card-heading card-hover text-info"
												data-toggle="collapse" data-target="#Schedule-project"
												aria-expanded="false" aria-controls="Schedule-project">
												<span> <i class="fa fa-calendar me-2"></i> Schedule
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div class="collapse" id="Schedule-project">
												<div class="card-body">
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click
															<strong class="text-success">Roadmap <i
																	class="fa fa-chevron-right"></i>
																Initiation List <i class="fa fa-chevron-right"></i>
																Click <i class="fa fa-file-text text-primary"></i>
																Details of any
																project <i class="fa fa-chevron-right"></i> Schedule
															</strong>.
														</li>

														<li class="list-group-item">Fill the details: <em>
																Milestone Activity, Started From, Milestone Month and
																Remarks. </em> <i class="fa fa-chevron-right"></i> Click
															<strong class="text-success">Submit.</strong>
															<h6 class="text-primary" style="text-indent: 20px;">
																You can edit/delete the schedule detail by clicking on
																<strong style="color: darkorange;">Edit/Delete</strong>
																button.
															</h6>
														</li>
													</ul>
												</div>
											</div>
										</div>
										<!-- Attachment -->
										<div class="card border-primary mb-4">
											<div class="card-header card-heading card-hover text-primary"
												data-toggle="collapse" data-target="#Attachments-project"
												aria-expanded="false" aria-controls="Attachments-project">
												<span> <i class="fa fa-paperclip me-2"></i> Attachment
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div class="collapse" id="Attachments-project">
												<div class="card-body">
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click
															<strong class="text-success">Roadmap <i
																	class="fa fa-chevron-right"></i>
																Initiation List <i class="fa fa-chevron-right"></i>
																Click <i class="fa fa-file-text text-primary"></i>
																Details of any
																project <i class="fa fa-chevron-right"></i> Attachment
															</strong>.
														</li>

														<li class="list-group-item">Enter FileName and Upload file
															</em> <i class="fa fa-chevron-right"></i> Click <strong
																class="text-success">Submit.</strong>

														</li>
													</ul>
												</div>
											</div>
										</div>
										<!-- Project Approval -->
										<div class="card border-info mb-4">
											<div class="card-header card-heading card-hover text-info"
												data-toggle="collapse" data-target="#Forward-Project"
												aria-expanded="false" aria-controls="Forward-Project">
												<span> <span class="fa-stack"> <i class="fa fa-file-text fa-stack-2x"
															style="color: #ccc; font-size: 1.4em;"></i> <i
															class="fa fa-pencil fa-stack-1x"
															style="color: #333; font-size: 1em;"></i>
													</span> Project Approval
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div class="collapse" id="Forward-Project">
												<div class="card-body">
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click <strong
																class="text-success">Roadmap <i
																	class="fa fa-chevron-right"></i> Initiation List <i
																	class="fa fa-chevron-right"></i> Click <span
																	class="fa-stack text-dark "> <i
																		class="fa fa-file-text fa-stack-1x"
																		style="color: #ccc; font-size: 20px;"></i> <i
																		class="fa fa-pencil fa-stack-1x"
																		style="color: #333; font-size: 16px;"></i>
																</span> Approval flow of any project
															</strong>.
														</li>
														<li class="list-group-item">Fill the details: <em>
																Subject, Comment, Initiated By, Officer Role,
																Recommended
																Officer , Approving Officer Role and Approving Officer.
															</em> <i class="fa fa-chevron-right"></i> Click <strong
																class="text-success">Submit</strong> <i
																class="fa fa-chevron-right"></i>
															Click <strong class="text-success">Forward</strong>.

															<h6 class="alert alert-danger mt-2">Note: One
																<strong>Recommending</strong> and
																<strong>Approving</strong> Officer is
																Mandatory.
															</h6>
															<div class="alert alert-info" role="alert">
																<i class="fa fa-info-circle me-2"></i>
																You can assign <strong>Approving</strong> Officer from
																another lab. <br>
																If <strong>Recommending</strong> officer has not
																recommended, you can
																revoke
																the request by clicking revoke button.
															</div>

														</li>
														<li class="list-group-item">Recommending Officer will
															navigate to notification, <br>
															<h6 class="ml-4 mb-2 text-dark font-weight-bold">Officer
																can:</h6>
															<ul class="list-group list-group-numbered">
																<li class="list-group-item"><strong
																		class="text-primary">
																		<i class="fa fa-eye"></i> Preview
																	</strong>: Recommending Officer will enter remark,
																	Officer can
																	<strong class="text-success">Recommend</strong> or
																	<strong class="text-danger">Return</strong>.
																</li>
																<li class="list-group-item"><strong
																		class="text-success">
																		<i class="fa fa-download"></i> Print Executive
																		Summary
																	</strong></li>
																<li class="list-group-item"><strong
																		class="text-primary">
																		<span class="fa-stack  "> <i
																				class="fa fa-square fa-stack-1x text-primary"
																				style="font-size: 25px;"></i> <i
																				class="fa fa-download fa-stack-1x text-white"
																				style="font-size: 16px;"></i>
																		</span> Print Approval Report
																	</strong></li>
															</ul>
														</li>
														<li class="list-group-item">Once recommended, Approving
															Officer will navigate to notification, <br>
															<h6 class="ml-4 mb-2 text-dark font-weight-bold">Officer
																can:</h6>
															<ul class="list-group list-group-numbered">
																<li class="list-group-item"><strong
																		class="text-primary">
																		<i class="fa fa-eye"></i> Preview
																	</strong>: Approving Officer will enter remark,
																	officer can <strong
																		class="text-success">Approve</strong> or <strong
																		class="text-danger">Return</strong>.</li>
																<li class="list-group-item"><strong
																		class="text-success">
																		<i class="fa fa-download"></i> Print Executive
																		Summary
																	</strong></li>
																<li class="list-group-item"><strong
																		class="text-primary">
																		<span class="fa-stack  "> <i
																				class="fa fa-square fa-stack-1x text-primary"
																				style="font-size: 25px;"></i> <i
																				class="fa fa-download fa-stack-1x text-white"
																				style="font-size: 16px;"></i>
																		</span> Print Approval Report
																	</strong></li>
															</ul>
														</li>

													</ul>
												</div>
											</div>
										</div>
										<!-- Checklist -->
										<div class="card border-primary mb-4">
											<div class="card-header card-heading card-hover text-primary"
												data-toggle="collapse" data-target="#Checklist-project"
												aria-expanded="false" aria-controls="Checklist-project">
												<span> <i class="fa fa-list-ol me-2"></i> Checklist / <i
														class="fa fa-eye"></i> Preview
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div class="collapse" id="Checklist-project">
												<div class="card-body">
													<h6 class="text-primary font-weight-bold">Preview Project</h6>
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click <strong
																class="text-success">Roadmap <i
																	class="fa fa-chevron-right"></i> Initiation List <i
																	class="fa fa-chevron-right"></i> Click <i
																	class="fa fa-eye text-primary"></i> Preview of any
																project
															</strong>.
														</li>
														<li class="list-group-item">You can view all the project
															related details. In <strong class="text-success"><i
																	class="fa fa-print"></i>
																Prints Tab</strong> , you can print project
															executive summary, project proposal, project presentation
															and
															can <i class="fa fa-download"></i> download project
															proposal.
														</li>
													</ul>

													<h6 class="text-primary font-weight-bold">Update Project
														Checklist</h6>
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click <strong
																class="text-success">Roadmap <i
																	class="fa fa-chevron-right"></i> Initiation List <i
																	class="fa fa-chevron-right"></i> Click <i
																	class="fa fa-list-ol text-primary"></i> Checklist of
																any
																project
															</strong>.
														</li>

														<li class="list-group-item">Each project includes a
															checklist where you can click to mark items as either 'Yes'
															or
															'No'.</li>
													</ul>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<!-- Ending line of Pre-Project Content -->

							<!-- Starting line of Committee Content -->
							<div id="Committee" class="content-section">
								<div class="card border-success">
									<div class="card-header bg-success text-white">
										<h5 class="ml-3">
											<i class="fa fa-users"></i> Committee User Guide
										</h5>
									</div>
									<div class="card-body">
										<div class="row">
											<div class="col col-md-9">
												<div id="userGuide">
													<!-- Section:Creating a committee -->
													<div class="card border-primary mb-4">
														<div class="card-header card-heading card-hover text-primary"
															data-toggle="collapse" data-target="#creating-committee"
															aria-expanded="true" aria-controls="creating-committee">
															<span> <i class="fa fa-plus-circle me-2"></i> Create a
																Committee
															</span> <i class="fa fa-chevron-down"></i>
														</div>
														<div id="creating-committee" class="collapse show">
															<div class="card-body ">
																<ul class="list-group list-group-numbered">
																	<li class="list-group-item">Click on <strong
																			class="text-success">Committee <i
																				class="fa fa-chevron-right"></i> New
																			Committee
																		</strong>.
																	</li>
																	<li class="list-group-item">Select Type: <strong
																			class="text-primary">Project</strong> or
																		<strong
																			class="text-primary">Non-Project</strong> <i
																			class="fa fa-chevron-right"></i> Click on
																		<strong class="text-info">Add
																			Committee</strong>.
																	</li>
																	<li class="list-group-item"><strong
																			class="text-info">Fill
																			the Details:</strong> <em> Committee Code,
																			Committee Name,
																			Guidelines, Purpose and Terms of Reference,
																			Committee
																			Type, Technical/Non-Technical
																			,Periodic/Non-Periodic. </em>
																		<i class="fa fa-chevron-right"></i> Click
																		<strong class="text-success">Submit.</strong>
																	</li>
																</ul>
															</div>
														</div>
													</div>
													<!-- Section:Editing a committee -->
													<div class="card border-info mb-4">
														<div class="card-header card-heading card-hover text-info"
															data-toggle="collapse" data-target="#edit-committee"
															aria-expanded="false" aria-controls="edit-committee">
															<span> <i class="fa fa-edit me-2"></i> Edit a Committee
															</span> <i class="fa fa-chevron-down toggle-arrow"></i>
														</div>
														<div id="edit-committee" class="collapse">
															<div class="card-body ">
																<h6 class="font-weight-bold text-primary">Non Project:
																</h6>
																<ul class="list-group list-group-numbered">
																	<li class="list-group-item">Click <strong
																			class="text-primary">Committee <i
																				class="fa fa-chevron-right"></i>
																			Non-Project.
																		</strong></li>
																	<li class="list-group-item">Click <strong
																			class="text-primary">Edit</strong> button <i
																			class="fa fa-chevron-right"></i>
																		<strong>Edit the
																			required fields</strong> <i
																			class="fa fa-chevron-right"></i>
																		Click
																		<strong class="text-success">Submit</strong>.
																	</li>

																</ul>
																<h6 class="font-weight-bold text-success">Project:</h6>
																<ul class="list-group list-group-numbered">
																	<li class="list-group-item">Click <strong
																			class="text-success">Committee <i
																				class="fa fa-chevron-right"></i> New
																			Committee
																		</strong> <i class="fa fa-chevron-right"></i>
																		Select Type as
																		<strong class="text-success"> Project.</strong>
																	</li>
																	<li class="list-group-item">Click <strong
																			class="text-success">
																			Edit</strong> button <i
																			class="fa fa-chevron-right"></i>
																		<strong>Edit the
																			required fields</strong> <i
																			class="fa fa-chevron-right"></i>
																		Click
																		<strong class="text-success">Submit.</strong>
																	</li>
																</ul>
															</div>
														</div>
													</div>
													<!-- Section:Constitute Members -->
													<div class="card border-success mb-4">
														<div class="card-header card-heading card-hover text-success"
															data-toggle="collapse" data-target="#constitute-members"
															aria-expanded="false" aria-controls="constitute-members">
															<span> <i class="fa fa-users me-2"></i> Constitute
																Committee - Non Project/ Division/ Initiation Project
															</span> <i class="fa fa-chevron-down"></i>
														</div>
														<div class="collapse" id="constitute-members">
															<div class="card-body">
																<div class="alert alert-info" role="alert">
																	<i class="fa fa-info-circle me-2"></i>
																	You can select
																	project/division/pre-project at the top left corner
																	that you
																	want to constitute.
																</div>
																<ul class="list-group list-group-numbered">
																	<li class="list-group-item">Click <strong
																			class="text-primary">Committee <i
																				class="fa fa-chevron-right"></i>
																			Non-Project/ Division/
																			Initiation/ Project
																		</strong> <i class="fa fa-chevron-right"></i>
																		<strong class="text-primary"><i
																				class="fa fa-eye"></i>
																			Constitution.</strong>
																	</li>

																	<li class="list-group-item">
																		<div class="alert alert-info" role="alert">
																			<i class="fa fa-info-circle me-2"></i>
																			<strong>Note: While constituting committee
																				for
																				Division/ Initiation/ Project, Select
																				and <strong class="text-success">Add
																					Committees.</strong>
																			</strong>
																		</div>
																		Assign Lab and Employee to each role:
																		Chairperson, Member
																		Secretary, Co-Chairperson, Proxy Member
																		Secretary.
																	</li>
																	<li class="list-group-item">Select whether committee
																		is
																		<strong class="text-success">pre-approved or
																			not.</strong>
																	</li>
																	<li class="list-group-item">Add Members <i
																			class="fa fa-chevron-right"></i> Select
																		Employees <i class="fa fa-chevron-right"></i>
																		Click <strong
																			class="text-success">Submit.</strong>
																	</li>
																	<li class="list-group-item">Update <strong
																			class="text-success">Serial No
																			List.</strong>
																		<h6 class="alert alert-info font-weight-bold">
																			Note:
																			Kindly
																			update the serial number list to ensure that
																			the employee
																			order is accurately reflected in the
																			reports.</h6>
																	</li>
																	<div class="alert alert-danger mt-2" role="alert">
																		<i class="fa fa-info-circle me-2"></i>
																		<strong>If
																			Committee
																			is Not Pre-Approved Follow these Additional
																			Steps
																		</strong>
																	</div>
																	<li class="list-group-item">Click <strong
																			class="text-primary">Preview</strong> <i
																			class="fa fa-chevron-right"></i> Select
																		<strong class="text-success">Recommending
																			Officer</strong> and
																		<strong class="text-success">Approving
																			Officer.</strong>
																		<div class="alert alert-info" role="alert">
																			<i class="fa fa-info-circle me-2"></i>
																			Note: One Approving Officer and Recommending
																			Officer
																			Mandatory.
																		</div>
																	</li>
																	<li class="list-group-item">Click <strong
																			class="text-warning">Update</strong> <i
																			class="fa fa-chevron-right"></i> <strong
																			class="text-success">Forward</strong> to
																		Recommending
																		Officer.
																		<div class="alert alert-info" role="alert">
																			<i class="fa fa-info-circle me-2"></i>
																			Note: If Recommending Officer has not
																			recommended ,the
																			user can revoke the request.
																		</div>
																	</li>

																	<li class="list-group-item">Recommending Officer or
																		Approving Officer will navigate to notification
																		or Click <strong class="text-success"> Approvals
																			<i class="fa fa-chevron-right"></i>
																			Committee Approvals <i
																				class="fa fa-chevron-right"></i> <i
																				class="fa fa-eye"></i>
																			Constitution <i
																				class="fa fa-chevron-right"></i> Enter
																			Remark <i class="fa fa-chevron-right"></i>
																			Click
																			Recommend/Approve or <strong
																				class="text-danger">Return</strong>.
																		</strong>
																	</li>
																</ul>
															</div>
														</div>
													</div>
													<!-- Section:AutoScheduling meeting for non-project -->
													<div class="card border-secondary mb-4">
														<div class="card-header card-heading card-hover text-danger"
															data-toggle="collapse" data-target="#auto-schedule"
															aria-expanded="false" aria-controls="auto-schedule">
															<span style="font-size: 16px;"> <i
																	class="fa fa-calendar me-2"></i>
																Auto-Schedule Meeting (Periodic Type)- Non-Project/
																Project/
																Initiation/ Division
															</span> <i class="fa fa-chevron-down"></i>
														</div>
														<div id="auto-schedule" class="collapse">
															<div class="card-body ">
																<ul class="list-group list-group-numbered">
																	<li class="list-group-item">Click <strong
																			class="text-success">
																			Committee <i
																				class="fa fa-chevron-right"></i>
																			Non-Project/
																			Division/
																			Initiation/ Project.
																		</strong>
																	</li>
																	<li class="list-group-item">Click <strong
																			class="text-success"> <i
																				class="fa fa-calendar me-2"></i>
																			Calendar Button/
																			Meeting
																			Schedule.
																		</strong></li>
																	<li class="list-group-item">Select <em>From
																			Date,To Date,Time</em> <i
																			class="fa fa-chevron-right"></i>
																		Click <strong class="text-success"> Submit
																		</strong>
																	</li>
																</ul>
															</div>
														</div>
													</div>
												</div>
											</div>
											<div class="col col-md-3">
												<!-- DOs and DON'Ts -->
												<div class="card scrollable">
													<div class="card-header bg-success text-white">
														<strong> Do's</strong>
													</div>
													<ul class="list-group list-group-numbered">
														<li class="list-group-item"><strong>Member
																secretary and Chair person </strong>is mandatory when
															constituting a
															committee.</li>
														<li class="list-group-item"><strong>PMRB</strong> and
															<strong>EB</strong>
															committees are default pre-approved.
														</li>
														<li class="list-group-item">While constituting the
															committee, if you select <strong>"Is Pre-Approved" =
																No</strong>, you must assign one <strong> Recommending
																Officer</strong> and one <strong> Approving
																Officer</strong> before
															forwarding for approval.
														</li>

														<li class="list-group-item">Always double-check committee
															details before submission.</li>
														<li class="list-group-item">Update serial number list
															after member addition.</li>
														<li class="list-group-item">Preview committee before
															sending for approval.</li>
													</ul>
												</div>

												<div class="card mt-3 scrollable">
													<div class="card-header bg-danger text-white">
														<strong> Don't Do's</strong>
													</div>

													<ul class="list-group list-group-numbered">
														<li class="list-group-item">The project code must be no
															more than six characters long.</li>
														<li class="list-group-item">You can't assign one person to
															multiple member type.</li>
														<li class="list-group-item">Don't skip the recommendation
															step if the committee is not pre-approved.</li>
														<li class="list-group-item">Don't forward the committee
															for approval unless all roles and responsibilities are
															correctly assigned.</li>
													</ul>


												</div>
											</div>

										</div>
									</div>
								</div>
							</div>
							<!-- Ending line of Committee Content -->

							<!-- Starting line of Schedule Content -->
							<div id="Schedule" class="content-section">
								<div class="card border-primary">
									<div class="card-header bg-primary text-white">
										<h5 class="ml-3">
											<i class="fa fa-calendar"></i> Schedule User Guide
										</h5>
									</div>
									<div class="card-body card-body-main">
										<div class="userguide">
											<!-- Section: Scheduling Meeting  -->
											<div class="card border-primary mb-4 ">
												<div class="card-header card-heading card-hover text-primary"
													data-toggle="collapse" data-target="#schedule-meeting"
													aria-expanded="true" aria-controls="schedule-meeting">
													<span> <i class="fa fa-calendar me-2"></i> Schedule
														Meeting
													</span> <i class="fa fa-chevron-down"></i>
												</div>

												<div id="schedule-meeting" class="collapse show">
													<div class="card-body">
														<ul class="list-group list-group-numbered">
															<li class="list-group-item">Click <strong
																	class="text-primary">Schedule</strong>, then choose
																either <strong
																	class="text-success">Non-Project</strong>, <strong
																	class="text-success">Project</strong>, <strong
																	class="text-success">Division</strong>, <strong
																	class="text-success">Pre-Project</strong> , <strong
																	class="text-success">Record of Discussion</strong>,
																<strong class="text-success">CARS</strong>, or <strong
																	class="text-success">Programme</strong> based on the
																type of
																meeting you want to create.
															</li>
															<li class="list-group-item">
																<ul class="list-group list-group-numbered">
																	<li class="list-group-item">If you have selected
																		<strong
																			class="text-success">Non-Project</strong>,
																		choose a
																		committee from the dropdown in the top-right
																		corner.
																	</li>
																	<li class="list-group-item">If you have selected
																		<strong class="text-success">Project /
																			Pre-Project /
																			Division / Programme / CARS:</strong> Choose
																		project/initiated
																		project/division/programme/CARS and committee
																		from the
																		dropdown in the top-left corner.
																	</li>
																	<li class="list-group-item">If you have selected
																		<strong class="text-success">Record of
																			Discussion</strong>: Choose
																		Project Type, Project and ROD name from the
																		dropdown in the
																		top left-corner.
																	</li>
																</ul>
															</li>
															<li class="list-group-item">Select <strong
																	class="text-danger">Date and
																	Time</strong>, then click <strong
																	class="text-info">Add
																	Schedule.</strong>
															</li>
															<div class="alert alert-info mt-2" role="alert">
																<i class="fa fa-info-circle me-2"></i>
																Right-side panel displays
																list of scheduled meetings for the committee.
															</div>
															<li class="list-group-item">You can delete the meeting by
																clicking above <strong class="text-danger"><i
																		class="fa fa-trash"></i>
																	Delete.</strong>
															</li>
															<li class="list-group-item">You can reschedule the
																meeting by changing <strong class="text-primary">
																	Meeting Date and Meeting Time </strong>.
															</li>
															<div class="alert alert-info mt-2" role="alert">
																<i class="fa fa-info-circle me-2"></i>
																Once Agenda Approved, can't
																delete the meeting.
															</div>
														</ul>
													</div>
												</div>
											</div>
											<!-- Section Schedule Detail -->
											<div class="card border-success mb-4">
												<div class="card-header card-hover text-success card-heading"
													data-toggle="collapse" data-target="#schedule-detail"
													aria-expanded="false" aria-controls="schedule-detail">
													<span><i class="fa fa-info-circle me-2"></i> Schedule
														Details</span> <i class="fa fa-chevron-down"></i>
												</div>
												<div class="collapse" id="schedule-detail">
													<div class="card-body">
														<div class="alert alert-info mt-2" role="alert">
															<i class="fa fa-info-circle me-2"></i>
															Member Secretary and
															Chairperson can only approve agenda. Notification will be
															sent
															to both.
														</div>
														<ul class="list-group list-group-numbered">
															<li class="list-group-item">Fill Agenda form: <em>Agenda
																	Items, References, Remarks, Lab,Presenter, Duration,
																	Attachment(if any)</em> <i
																	class="fa fa-chevron-right"></i> Click
																<strong class="text-success">Submit.</strong>
															</li>
															<li class="list-group-item">Enter <em>Venue,
																	Briefing of the meeting, References</em> <i
																	class="fa fa-chevron-right"></i> Click <strong
																	class="text-warning font-weight-bold">Update.</strong>
															</li>
															<div class="alert alert-info mt-2" role="alert">
																<i class="fa fa-info-circle me-2"></i>
																You can edit agenda by clicking on <strong
																	class="text-primary font-weight-bold">Edit
																	Button.</strong>
															</div>

															<li class="list-group-item">Click <strong
																	class="text-success">Invite</strong> <i
																	class="fa fa-chevron-right"></i> Select Members <i
																	class="fa fa-chevron-right"></i> Click <strong
																	class="text-success">Submit.</strong>
															</li>
															<li class="list-group-item">Click <strong
																	class="text-success">Send
																	Invitation</strong> to notify
																members via email.
															</li>
															<li class="list-group-item">Add additional members or
																representatives using respective options.</li>
														</ul>
													</div>
												</div>
											</div>
											<!-- Section Agenda Approval -->
											<div class="card border-info  mb-4">
												<div class="card-header card-hover text-info card-heading "
													data-toggle="collapse" data-target="#agenda-approval"
													aria-expanded="false" aria-controls="agenda-approval">
													<span><i class="fa fa-pencil-square-o me-2"></i> Agenda
														Approval</span> <i class="fa fa-chevron-down toggle-arrow"></i>
												</div>
												<div id="agenda-approval" class="collapse">
													<div class="card-body">
														<ul class="list-group list-group-numbered">
															<li class="list-group-item">Click <strong
																	class="text-success">Agenda
																	Approval </strong> <i
																	class="fa fa-chevron-right"></i> Notification
																will be sent to
																Chairperson and Member Secretary.
															</li>
															<li class="list-group-item">Member Secretary or
																Chairperson can navigate to notification or Click
																<strong class="text-primary"> Approvals <i
																		class="fa fa-chevron-right"></i>
																	Meetings <i class="fa fa-chevron-right"></i> <i
																		class="fa fa-eye"></i>
																	Details.
																</strong>
															</li>
															<li class="list-group-item">Enter <strong
																	class="text-success">Remark</strong>. They can
																<strong class="text-success">Approve</strong> or <strong
																	class="text-danger">Return.</strong>
															</li>
															<li class="list-group-item">If returned,view <strong
																	class="text-success">Reason for Returning</strong>
																update,
																and click <strong class="text-primary">Re-submit
																	Agenda Approval.</strong>
															</li>
														</ul>
													</div>
												</div>
											</div>
											<!-- Section Kickoff Meeting-->
											<div class="card border-success mb-4">
												<div class="card-header card-hover text-success card-heading"
													data-toggle="collapse" data-target="#kickoff-meeting"
													aria-expanded="false" aria-controls="kickoff-meeting">
													<span><i class="fa fa-play-circle me-2"></i> Kickoff
														Meeting</span> <i class="fa fa-chevron-down"></i>
												</div>
												<div id="kickoff-meeting" class="collapse">
													<div class="card-body">
														<ul class="list-group list-group-numbered">
															<li class="list-group-item">Click <strong
																	class="text-success">Kickoff
																	Meeting.</strong>
															</li>
															<li class="list-group-item text-danger"><strong>OTP
																</strong>will be sent to Chairperson or Member
																Secretary(Only for
																same-day meeting).</li>
															<li class="list-group-item">Enter OTP <i
																	class="fa fa-chevron-right"></i>
																Click <strong class="text-primary">Validate</strong> or
																<strong class="text-success">Resend OTP.</strong>
															</li>
														</ul>
													</div>
												</div>
											</div>
											<!-- Section Attendance-->
											<div class="card border-warning mb-4">
												<div class="card-header card-hover text-dark card-heading"
													data-toggle="collapse" data-target="#attendance"
													aria-expanded="false" aria-controls="attendance">

													<span><span class="fa-stack"
															style="position: relative; font-size: 1em;"> <i
																class="fa fa-user fa-stack-1x text-primary"
																style="font-size: 25px;"></i> <i
																class="fa fa-check-circle text-success"
																style="position: absolute; bottom: -1px; right: -3px; font-size: 14px;"></i>
														</span> Attendance</span> <i class="fa fa-chevron-down"></i>
												</div>
												<div id="attendance" class="collapse">
													<div class="card-body">
														<ul class="list-group list-group-numbered">
															<li class="list-group-item">Click <strong
																	class="text-success">Attendance.</strong></li>
															<li class="list-group-item">Mark attendance for each
																member (Present/Absent).</li>

															<li class="list-group-item">Click <strong
																	class="text-warning">Update.</strong> <i
																	class="fa fa-chevron-right"></i> Click <strong
																	class="text-success">Back.</strong>
																<h6 class="alert alert-danger ">
																	Kindly update the
																	serial number list to ensure that the employee order
																	is
																	accurately reflected in the reports.</h6>
															</li>
														</ul>
													</div>
												</div>
											</div>
											<!-- Section Minutes-->
											<div class="card border-secondary mb-4">
												<div class="card-header card-hover text-secondary card-heading"
													data-toggle="collapse" data-target="#mom" aria-expanded="false"
													aria-controls="mom">
													<span><i class="fa fa-file-text me-2"></i> Minutes of
														Meeting</span> <i class="fa fa-chevron-down"></i>
												</div>
												<div id="mom" class="collapse">
													<div class="card-body">
														<ul class="list-group list-group-numbered">
															<li class="list-group-item">Click <strong
																	class="text-primary">Minutes.</strong>
															</li>
															<li class="list-group-item">Fill fields: <em>Introduction,
																	Opening Remarks,</em><br> <em
																	class="text-success">Agenda
																	<ul class="list-group list-group-numbered">
																		<li class="list-group-item">Presentation and
																			Discussion : Click Add button <i
																				class="fa fa-chevron-right"></i>Enter
																			Action Name,
																			Remark
																			(if any). Click Submit. </i>
																		</li>
																		<div class="alert alert-info mt-2" role="alert">
																			<i class="fa fa-info-circle me-2"></i>
																			Outcome is
																			classified
																			into six types: Action, Comment, Decision,
																			Issue,
																			Recommendation, and Risk.
																		</div>
																		<li class="list-group-item">Outcomes: Select the
																			outcome type from the dropdown menu <i
																				class="fa fa-chevron-right"></i> Click
																			Add <i class="fa fa-chevron-right"></i>
																			Enter Action Name,
																			Remark
																			(if any). Click Submit.
																		</li>
																	</ul>
																</em>, <em> Other Discussion, Other Outcomes: Similar to
																	the
																	agenda outcomes section, Conclusion <i
																		class="fa fa-chevron-right"></i>Click <strong
																		class="text-success">Submit.</strong>
																</em>
															</li>
														</ul>
													</div>
												</div>
											</div>
											<!-- Section action assignment -->
											<div class="card border-danger mb-4">
												<div class="card-header card-hover text-danger card-heading"
													data-toggle="collapse" data-target="#action-assignment"
													aria-expanded="false" aria-controls="action-assignment">
													<span><i class="fa fa-tasks me-2"></i> Action Assignment</span>
													<i class="fa fa-chevron-down"></i>
												</div>
												<div id="action-assignment" class="collapse">
													<div class="card-body">
														<ul class="list-group list-group-numbered">
															<li class="list-group-item">Click <strong
																	class="text-primary">Assign Action
																	<i class="fa fa-thumbs-up"></i>.
																</strong></li>
															<div class="alert alert-info mt-2" role="alert">
																<i class="fa fa-info-circle me-2"></i>
																On the
																left-hand side, you can choose the action you want to
																assign
																by clicking on it.
															</div>
															<li class="list-group-item">Enter <em>PDC,Priority,
																	Category ,Lab, Assignee(s) </em> <i
																	class="fa fa-chevron-right"></i>
																Click <strong class="text-success">Submit.</strong>
																<div class="alert alert-info" role="alert">
																	<i class="fa fa-info-circle me-2"></i>
																	Actions can be
																	assigned to multiple employees and to another lab as
																	well.
																</div>
															</li>
															<li class="list-group-item"><strong
																	class="text-primary">Edit
																</strong>or <strong class="text-danger">Delete
																</strong>actions using
																buttons in the action list.</li>
														</ul>
													</div>
												</div>
											</div>
											<!-- Section MOM Approvals-->
											<div class="card border-dark mb-4">
												<div class="card-header card-hover text-dark card-heading"
													data-toggle="collapse" data-target="#mom-approval"
													aria-expanded="false" aria-controls="mom-approval">
													<span><i class="fa fa-check-circle me-2"></i> Minutes of
														Approval</span> <i class="fa fa-chevron-down"></i>
												</div>
												<div id="mom-approval" class="collapse">
													<div class="card-body">
														<ul class="list-group list-group-numbered">
															<div class="alert alert-info" role="alert">
																<i class="fa fa-info-circle me-2"></i>
																Once the MOM is approved,
																no further changes can be made.
															</div>
															<li class="list-group-item"><strong
																	class="text-primary">Click
																	<i class="fa fa-chevron-right"></i> Minutes
																	Approval.
																</strong></li>

															<li class="list-group-item">Enter Officer role and select
																<strong class="text-success"> Recommending Officers
																	and Approving Officers . </strong>
																<div class="alert alert-info mt-2" role="alert">
																	<i class="fa fa-info-circle me-2"></i>
																	One Recommending Officer and Approving Officer is
																	Mandatory
																</div>
															</li>
															<li class="list-group-item">Click <strong
																	class="text-warning">Update</strong> <i
																	class="fa fa-chevron-right"></i> <strong
																	class="text-success">Forward.</strong>
																Can Revoke the request also, by clicking <strong
																	class="text-danger">Revoke.</strong>
															</li>
															<li class="list-group-item">
																<div class="alert alert-info" role="alert">
																	<i class="fa fa-info-circle me-2"></i>
																	Recommending Officer will get Notification
																</div>
																Recommending
																Officer will navigate to notification or Click <strong
																	class="text-success">Approvals <i
																		class="fa fa-chevron-right"></i>
																	Mom Approvals <i class="fa fa-chevron-right"></i> <i
																		class="fa fa-eye"></i></strong>
															</li>
															<li class="list-group-item">Enter Remark.Recommending
																Officer can <strong
																	class="text-success">Recommend</strong>
																or <strong class="text-danger">Return.</strong>
															</li>
															<li class="list-group-item">Once Recommended,
																Notification will be sent to Approving Officer.
																Approving
																Officer can navigate to notification or Click <strong
																	class="text-success">Approvals <i
																		class="fa fa-chevron-right"></i>
																	Mom Approvals <i class="fa fa-chevron-right"></i> <i
																		class="fa fa-eye"></i></strong>.
																Enter Remark, Approving Officer can <strong
																	class="text-success">Approve</strong> or <strong
																	class="text-danger">Return.</strong>
															</li>
														</ul>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<!-- Ending line of Schedule Content -->

							<!-- Starting line of Action Content -->
							<div id="Action" class="content-section">
								<div class="card border-info ">
									<div class="card-header bg-info text-white">
										<h5 class="ml-3">
											<i class="fa fa-tasks"></i> Action User Guide
										</h5>
									</div>
									<div class="card-body">
										<!-- Section-1:Creating a new action item -->
										<div class="card border-primary mb-4">
											<div class="card-header card-heading card-hover text-primary"
												data-toggle="collapse" data-target="#create-action-item"
												aria-expanded="true" aria-controls="create-action-item">
												<span><i class="fa fa-plus-circle me-2"></i> Creating a
													New Action/ Issue/ Risk Item</span> <i
													class="fa fa-chevron-down toggle-arrow"></i>
											</div>
											<div id="create-action-item" class="collapse show">
												<div class="card-body">
													<div class="alert alert-info" role="alert">
														<i class="fa fa-info-circle me-2"></i>
														Only users
														with higher-level roles can assign action items.</strong>
													</div>
													<ul class="list-group list-group-numbered ">
														<li class="list-group-item">Click <strong
																class="text-primary">Action Items <i
																	class="fa fa-chevron-right"></i> New Action.
															</strong></li>
														<li class="list-group-item">Enter <strong
																class="text-success">Action Item, PDC,
																Project,
																Action Type (Issue,Risk,Action), Priority and
																Category.</strong>

														</li>
														<li class="list-group-item">Click <strong
																class="text-success">Submit.</strong>
														</li>

														<li class="list-group-item">
															<div class="alert alert-info" role="alert">
																<i class="fa fa-info-circle me-2"></i>
																Actions
																can be assigned to multiple employees and to another lab
																as
																well.</strong>
															</div>
															Select <strong class="text-success">Lab,
																Assignee</strong> and upload <strong
																class="text-success">attachment</strong>
															(if any).
														</li>
														<h6 class="text-primary">To Edit Action Item</h6>
														<li class="list-group-item">Scroll down to the <strong
																class="text-primary">Action Assigned list</strong>,
															locate the
															action item, and click <strong class="text-primary"><i
																	class="fa fa-edit"></i> Edit </strong> (editing is
															allowed only
															until
															the assignee has viewed it), a new window will appear where
															you can make your changes.
														</li>
														<h6 class="text-primary">To perform additional operations
															(Re-assign, Extend PDC,Update Progress)</h6>
														<li class="list-group-item">Scroll down to the <strong
																class="text-primary">Action Assigned list</strong>,
															locate the
															action item, and click <strong class="text-primary"><i
																	class="fa fa-eye"></i> Actions </strong> <i
																class="fa fa-chevron-right"></i> Select Assignee/Select
															PDC/ <em>Select
																Progress Date, Enter progress, Attachment and
																Remarks</em> and
															Click <strong class="text-warning"> Reassign</strong>/
															<strong class="text-success">Extend PDC</strong> / <strong
																class="text-success">Submit</strong>.
														</li>
													</ul>
												</div>
											</div>
										</div>
										<!-- Section-1:Update Work Progress -->
										<div class="card border-success mb-4">
											<div class="card-header card-heading card-hover text-success"
												data-toggle="collapse" data-target="#update-work-progress"
												aria-expanded="true" aria-controls="update-work-progress">
												<span><i class="fa fa-tasks me-2"></i> Update Work
													Progress (Action/Issue/Risk)</span> <i
													class="fa fa-chevron-down toggle-arrow"></i>
											</div>
											<div id="update-work-progress" class="collapse">
												<div class="card-body">
													<ul class="list-group list-group-numbered ">
														<div class="alert alert-info" role="alert">
															<i class="fa fa-info-circle me-2"></i>
															Action item
															can be from Meetings also.
														</div>
														<li class="list-group-item">Click <strong
																class="text-primary">Action items <i
																	class="fa fa-chevron-right"></i> To Act <i
																	class="fa fa-chevron-right"></i> <i
																	class="fa fa-eye"></i>
																Details.
															</strong></li>
														<li class="list-group-item">Fill the details <em>As
																on date, Remarks, Progress, Attachment(if any).</em> <i
																class="fa fa-chevron-right"></i> Click <strong
																class="text-success">Submit.</strong> <i
																class="fa fa-chevron-right"></i> Click <strong
																class="text-success">Action Forward.</strong>
															<h6 class="text-danger">Note: Higher Official can send
																back also.</h6>
														</li>
														<li class="list-group-item">Click <strong
																class="text-primary"><i class="fa fa-refresh"></i>
																Distribute </strong> to distribute action items with
															other employees.
														</li>
														<li class="list-group-item">Click <strong
																class="text-success"><i class="fa fa-sitemap"></i>
																Tree</strong> to view the action tree.
														</li>
													</ul>
												</div>
											</div>
										</div>
										<!-- Section-3:Review and Close Action Item -->
										<div class="card border-info mb-4">
											<div class="card-header card-heading card-hover text-info"
												data-toggle="collapse" data-target="#review-close" aria-expanded="true"
												aria-controls="review-close">
												<span><i class="fa fa-eye me-2"></i> Review and Close
													(Action/Issue/Risk)</span> <i
													class="fa fa-chevron-down toggle-arrow"></i>
											</div>
											<div id="review-close" class="collapse">
												<div class="card-body">
													<ul class="list-group list-group-numbered ">
														<li class="list-group-item">Click <strong
																class="text-primary">Action items <i
																	class="fa fa-chevron-right"></i> To Review <i
																	class="fa fa-chevron-right"></i> <i
																	class="fa fa-eye"></i>
																Details.
															</strong></li>
														<li class="list-group-item">Enter <em> Remarks </em> <i
																class="fa fa-chevron-right"></i> Click <strong
																class="text-danger">Close
																Action Item </strong> / <strong
																class="text-warning">Send Back </strong>
															to close action item
															or to return back.
														</li>
														<h6 class="text-primary">To Extend PDC</h6>
														<li class="list-group-item">Select PDC <i
																class="fa fa-chevron-right"></i> Click
															<strong class="text-success">Update.</strong>
														</li>
														<h6 class="text-primary">To view Action Tree</h6>
														<li class="list-group-item">Click <strong
																class="text-success"><i class="fa fa-sitemap"></i>
																Action Tree</strong> to view Action Tree.
														</li>
													</ul>
												</div>
											</div>
										</div>
										<!-- Section-4:Reports -->
										<div class="card border-secondary mb-4">
											<div class="card-header card-heading card-hover text-secondary"
												data-toggle="collapse" data-target="#action-report" aria-expanded="true"
												aria-controls="action-report">
												<span><i class="fa fa-bar-chart me-2"></i> Reports</span> <i
													class="fa fa-chevron-down toggle-arrow"></i>
											</div>
											<div id="action-report" class="collapse">
												<div class="card-body">
													<div class="alert alert-info" role="alert">
														<i class="fa fa-info-circle me-2"></i>
														You can view
														all the action report according to status
														(ex:In-Progress,All,Not-started etc)
													</div>
													<ul class="list-group list-group-numbered ">
														<li class="list-group-item">Click <strong
																class="text-primary">Reports <i
																	class="fa fa-chevron-right"></i> Action Report.
															</strong></li>
														<li class="list-group-item">
															<div class="alert alert-info" role="alert">
																<i class="fa fa-info-circle me-2"></i>
																Based on the user's login
																role, the project list and assigned employees will be
																displayed accordingly.
															</div>
															At the top of the screen,
															Filter based
															on <em class="text-success">Project, Assignee, Type
																(Action,Milestone,Meeting), Status
																(Inprogress,Not-Started,Closed) </em> <i
																class="fa fa-chevron-right"></i> Click <i
																class="fa fa-download text-danger"></i> button.
														</li>
													</ul>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<!-- Ending line of Action Content -->

							<!-- Starting line of Milestone Content -->
							<div id="Milestone" class="content-section">
								<div class="card border-danger ">
									<div class="card-header bg-danger text-white">
										<h5 class="ml-3">
											<i class="fa fa-flag-checkered"></i> Milestone User Guide
										</h5>
									</div>
									<div class="card-body">
										<!-- Creating a Milestone -->
										<div class="card border-danger mb-4">
											<div class="card-header card-heading card-hover text-danger"
												data-toggle="collapse" data-target="#creating-milestone"
												aria-expanded="true" aria-controls="creating-milestone">
												<span> <i class="fa fa-flag-checkered me-2"></i> Creating
													a milestone
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div id="creating-milestone" class="collapse show">
												<div class="card-body">
													<div class="alert alert-info" role="alert">
														<i class="fa fa-info-circle me-2"></i>
														Only Project Director or
														Admin can add Milestone activity.
													</div>
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click <i
																class="fa fa-chevron-right"></i> <strong
																class="text-primary">Milestone</strong>
															<i class="fa fa-chevron-right"></i> <strong
																class="text-primary">New
																Activity</strong> <i class="fa fa-chevron-right"></i>
															<strong class="text-primary">Select
																Project</strong> <i class="fa fa-chevron-right"></i>
															<strong class="text-primary"> Add Activity. </strong>
														</li>

														<li class="list-group-item"><strong class="text-info">
																<div class="alert alert-info" role="alert">
																	<i class="fa fa-info-circle me-2"></i>
																	Check 'All' to load all employees.
																</div>
																Enter the
																details:
															</strong> <em> Activity name, Type, From, To, First OIC,
																Second OIC
															</em> <i class="fa fa-chevron-right"></i> Click <strong
																class="text-success">Submit.</strong></li>
														<li class="list-group-item">Fill out the sub-activities
															section as well. Click <strong class="text-success">
																Submit </strong> after completing each sub-activity
															details.
														</li>
														<div class="alert alert-info" role="alert">
															<i class="fa fa-info-circle me-2"></i>
															Project
															Director, the Admin, and the OICs of the Parent Milestone
															are
															authorized to add, edit and assigning weights to milestones.
														</div>
													</ul>
												</div>
											</div>
										</div>
										<!-- Assigning weights to  a Milestone -->
										<div class="card border-primary mb-4">
											<div class="card-header card-heading card-hover text-primary"
												data-toggle="collapse" data-target="#assigning-weights"
												aria-expanded="true" aria-controls="assigning-weights">
												<span><i class="fa fa-balance-scale me-2"></i> Assigning
													weightage to milestone</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div id="assigning-weights" class="collapse">
												<div class="card-body">
													<ul class="list-group list-group-numbered">
														<div class="alert alert-info" role="alert">
															<i class="fa fa-info-circle me-2"></i>
															Note: Ensure that the total
															weightage for each of milestones, activities, and
															sub-activities is 100.
														</div>
														<li class="list-group-item">Click <i
																class="fa fa-chevron-right"></i> <strong
																class="text-success">Milestone</strong>
															<i class="fa fa-chevron-right"></i> <strong
																class="text-success">New
																Activity</strong> <i class="fa fa-chevron-right"></i>
															<strong class="text-success">Select
																Project</strong> <i class="fa fa-chevron-right"></i>
															<strong class="text-success"> Edit weightage. </strong>
														</li>
														<li class="list-group-item"><strong class="text-info">Enter
																the weightage for each activity and
																sub-activity.</strong></li>
														<li class="list-group-item">Click <strong
																class="text-success">Edit
																Button.</strong>
														</li>
													</ul>
												</div>
											</div>
										</div>
										<!-- Assigning milestone to employees -->
										<div class="card border-success mb-4">
											<div class="card-header card-heading card-hover text-success"
												data-toggle="collapse" data-target="#assigning-employees"
												aria-expanded="true" aria-controls="assigning-employees">
												<span> <i class="fa fa-user-plus me-2"></i> Assigning
													milestone to employees
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div id="assigning-employees" class="collapse">
												<div class="card-body">
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click <i
																class="fa fa-chevron-right"></i> <strong
																class="text-danger">Milestone</strong>
															<i class="fa fa-chevron-right"></i> <strong
																class="text-danger">New
																Activity</strong> <i class="fa fa-chevron-right"></i>
															<strong class="text-danger">Select
																Project</strong> <i class="fa fa-chevron-right"></i>
															<strong class="text-danger"> Assign </strong>.
														</li>
														<li class="list-group-item"><strong class="text-info">Respected
																OIC's will navigate to Milestone <i
																	class="fa fa-chevron-right"></i>
																Assignee <i class="fa fa-chevron-right"></i>
															</strong> <strong class="text-success">Accept</strong> or
															<strong class="text-danger">Send Back.</strong>
														</li>
													</ul>
												</div>
											</div>
										</div>
										<!-- Update Milestone Activity -->
										<div class="card border-warning mb-4">
											<div class="card-header card-heading card-hover text-dark"
												data-toggle="collapse" data-target="#update-activity"
												aria-expanded="true" aria-controls="update-activity">
												<span> <i class="fa fa-wrench me-2"></i> Update Activity
													items
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div id="update-activity" class="collapse">
												<div class="card-body">
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Employees can navigate to
															notification or Click <strong class="text-danger">Milestone
																<i class="fa fa-chevron-right"></i> Assignee <i
																	class="fa fa-chevron-right"></i> Click Settings
																icon(Update).
															</strong>
														</li>
														<li class="list-group-item"><strong class="text-primary">Enter
																the Details:</strong> <br> <em> Progress, Progress Date,
																Attach file (if any) and Remarks. Click <strong
																	class="text-success">Submit.</strong>
															</em>
															<div class="alert alert-info" role="alert">
																<i class="fa fa-info-circle me-2"></i>
																Select an employee from the right side to reassign the
																activity to another person.
															</div>
														</li>

													</ul>
												</div>
											</div>
										</div>
										<!-- Set and Compare Baseline -->
										<div class="card border-info mb-4">
											<div class="card-header card-heading card-hover text-info"
												data-toggle="collapse" data-target="#set-and-compare"
												aria-expanded="true" aria-controls="set-and-compare">
												<span><i class="fa fa-bar-chart me-2"></i> Set Baseline</span> <i
													class="fa fa-chevron-down"></i>
											</div>
											<div id="set-and-compare" class="collapse">
												<div class="card-body">
													<ul class="list-group list-group-numbered">

														<li class="list-group-item">Respected OIC's of the parent
															milestone, Project director and Admin can set baseline and
															will navigate to <strong class="text-danger">
																Milestone <i class="fa fa-chevron-right"></i> New
																Activity <i class="fa fa-chevron-right"></i>
															</strong> <strong class="text-info">Baseline</strong>.
														</li>
														<li class="list-group-item">Make changes specific to that
															particular milestone.</li>
														<li class="list-group-item">Click <strong
																class="text-warning font-weight-bold">Update
																Icon,</strong>
															and then Click <strong class="text-success">Set
																Baseline </strong> Button.
														</li>
														<div class="alert alert-info mt-2" role="alert">
															<i class="fa fa-info-circle me-2"></i>
															After any change in milestone,
															baseline should be set to get it displayed latest data.
														</div>
													</ul>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<!-- Ending line of Milestone Content -->

							<!-- Starting line of Procurement Content -->
							<div id="Procurement" class="content-section">
								<div class="card" style="border-color: #4b7bec;">
									<div class="card-header text-white" style="background-color: #4b7bec;">
										<h5 class="ml-3">
											<i class="fa fa-shopping-cart"></i> Procurement
										</h5>
									</div>

									<div class="card-body">
										<div class="alert alert-info" role="alert">
											<i class="fa fa-info-circle me-2"></i>
											Note: You can create
											a procurement in four ways: by selecting existing demands from
											IBAS, by manually entering individual product demands, by creating
											envisaged (future) demands, or by bulk uploading demands using the
											Excel template provided.
										</div>
										<!-- Add Demand Form IBAS -->
										<div class="card border-primary mb-4">
											<div class="card-header card-heading card-hover text-primary"
												data-toggle="collapse" data-target="#procurement-ibas"
												aria-expanded="true" aria-controls="procurement-ibas">
												<span> <i class="fa fa-shopping-cart me-2"></i> Add Demand
													From IBAS
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div class="collapse-show " id="procurement-ibas">
												<div class="card-body">
													<ul class="list-group list-group-numbered">
														<div class="alert alert-info" role="alert">
															<i class="fa fa-info-circle me-2"></i>
															At the top right corner, you
															can select the project for which you want to create the
															procurement.
														</div>
														<li class="list-group-item">Click <strong class="text-success">
																Procurement <i class="fa fa-chevron-right"></i>
																Procurement Status <i class="fa fa-chevron-right"></i>
																Add Demand From IBAS.
															</strong></li>
														<h6 class="alert alert-danger mt-2">You can select
															one demand at a time.</h6>
														<li class="list-group-item">A list of demand's will be
															displayed. Click on <strong class="text-success"><i
																	class="fa fa-plus"></i></strong> to add the demand.
														</li>
														<h6 class="text-primary">To update demand status</h6>
														<li class="list-group-item">Click <strong
																class="text-primary"><i class="fa fa-eye"></i> Demand
																Status</strong> <i class="fa fa-chevron-right"></i>
															<em>Select
																Procurement Status, Enter Event Date and Remarks.</em>
															<i class="fa fa-chevron-right"></i> Click <strong
																class="text-success">Submit.</strong>
														</li>
														<h6 class="text-primary">To inactive demand</h6>
														<li class="list-group-item">Click <strong class="text-danger"><i
																	class="fa fa-times"></i>
																Demand Inactive</strong>.
														</li>
														<div class="alert alert-info mt-2" role="alert">
															<i class="fa fa-info-circle me-2"></i>
															Note: When the demand reaches the Order Placement stage, you
															can view the order details by clicking on <strong
																class="text-success">Order
																View</strong> button.
														</div>
													</ul>
												</div>
											</div>
										</div>
										<!-- Manual Demand -->
										<div class="card border-info mb-4">
											<div class="card-header card-heading card-hover text-info"
												data-toggle="collapse" data-target="#manual-demand" aria-expanded="true"
												aria-controls="manual-demand">
												<span> <i class="fa fa-pencil me-2"></i> Manual Demand
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div class="collapse " id="manual-demand">
												<div class="card-body">
													<ul class="list-group list-group-numbered">
														<div class="alert alert-info" role="alert">
															<i class="fa fa-info-circle me-2"></i>
															At the top right corner, you
															can select the project for which you want to create the
															procurement.
														</div>
														<li class="list-group-item">Click <strong class="text-success">
																Procurement <i class="fa fa-chevron-right"></i>
																Procurement Status <i class="fa fa-chevron-right"></i>
																Manual Demand.
															</strong></li>
														<h6 class="alert alert-danger mt-2">You can add one
															demand at a time and Demand No should be unique.</h6>
														<li class="list-group-item">Fill the details: <em>Demand
																No, Demand Date, Estimated Cost(In Rupees), Item
																Name</em> <i class="fa fa-chevron-right"></i> Click
															<strong class="text-success">Submit.</strong>
														</li>
														<h6 class="text-primary">To Edit Demand</h6>
														<li class="list-group-item">Click <strong
																class="text-primary"><i class="fa fa-edit"></i> Edit
																Demand</strong> <i class="fa fa-chevron-right"></i>
															<strong>Edit
																the required details</strong> <i
																class="fa fa-chevron-right"></i>
															Click <strong class="text-success">Submit.</strong>
														</li>
														<h6 class="text-primary">To update demand status</h6>
														<li class="list-group-item">Click <strong
																class="text-primary"><i class="fa fa-eye"></i> Demand
																Status</strong> <i class="fa fa-chevron-right"></i>
															<em>Select
																Procurement Status, Enter Event Date and Remarks.</em>
															<i class="fa fa-chevron-right"></i> Click <strong
																class="text-success">Submit.</strong>
															<div class="alert alert-info mt-2" role="alert">
																<i class="fa fa-info-circle me-2"></i>
																Fill in the Order Details
																when the demand reaches the Order Placement stage.
															</div>
															<br>
															<h6 class="text-primary">Instructions for Order Placement
																Stage</h6>
															<ul class="list-group-numbered list-group">
																<li class="list-group-item">Fill the details: Order No,
																	Order Date, Order Cost, DP Date, Item for and Vendor
																	<i class="fa fa-chevron-right"></i> Click <strong
																		class="text-success">Submit.</strong>
																</li>
																<h6 class="text-primary">
																	To add multiple orders, Click on <i
																		class="fa fa-plus"></i>
																	icon.
																</h6>
																<h6 class="text-success">To update order</h6>
																<li class="list-group-item">Click <strong
																		class="text-success">Order
																		View</strong> button <i
																		class="fa fa-chevron-right"></i> Edit
																	the required details <i
																		class="fa fa-chevron-right"></i> Click
																	<strong class="text-primary">Update</strong>.
																</li>
															</ul>
														</li>
														<h6 class="text-primary">To inactive demand</h6>
														<li class="list-group-item">Click <strong class="text-danger"><i
																	class="fa fa-times"></i>
																Demand Inactive</strong>.
														</li>
														<h6 class="text-primary">Procurement Milestone</h6>
														<br>
														<h6 class="text-success">To Add Procurement Milestone</h6>
														<li class="list-group-item">Click <strong
																class="text-primary"><i class="fa fa-list-ul"></i>
																Add Procurement Milestone</strong> <i
																class="fa fa-chevron-right"></i>
															Select Probable Date for each status <i
																class="fa fa-chevron-right"></i>
															Click <strong class="text-success">Add.</strong>
														</li>
														<h6 class="text-success">To Edit Procurement Milestone</h6>
														<li class="list-group-item">Click <strong
																class="text-primary"><i class="fa fa-list-ul"></i>
																Add Procurement Milestone</strong> <i
																class="fa fa-chevron-right"></i>
															Select Probable Date for each status <i
																class="fa fa-chevron-right"></i>
															Click <strong class="text-success">Edit.</strong>
														</li>
														<h6 class="text-success">To Set Baseline</h6>
														<li class="list-group-item">Click <strong
																class="text-primary"><i class="fa fa-list-ul"></i>
																Add Procurement Milestone</strong> <i
																class="fa fa-chevron-right"></i>
															Select Probable Date for each status <i
																class="fa fa-chevron-right"></i>
															Click <strong class="text-success">Set Base Line.</strong>
														</li>
														<h6 class="text-success">To View Procurement Milestone</h6>
														<li class="list-group-item">Click on <strong
																class="text-primary">View</strong>
															button to view the
															procurement milestone dashboard. <br> The procurement
															milestone dashboard is
															divided into four quarters, displaying: The Probable Date
															(planned milestone date) The Actual Date (when the milestone
															was actually completed) A comparison showing whether the
															probable and actual dates fall in the same quarter This
															helps
															track progress against plan and identify delays or early
															completions within the procurement cycle.
														</li>
													</ul>
												</div>
											</div>
										</div>
										<!-- Envisaged Demand -->
										<div class="card border-success mb-4">
											<div class="card-header card-heading card-hover text-success"
												data-toggle="collapse" data-target="#envisaged-demand"
												aria-expanded="true" aria-controls="envisaged-demand">
												<span> <i class="fa fa-lightbulb-o me-2"></i> Envisaged
													Demand
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div class="collapse " id="envisaged-demand">
												<div class="card-body">
													<ul class="list-group list-group-numbered">
														<div class="alert alert-info" role="alert">
															<i class="fa fa-info-circle me-2"></i>
															At the top right corner, you
															can select the project for which you want to create the
															procurement.
														</div>
														<li class="list-group-item">Click <strong class="text-success">
																Procurement <i class="fa fa-chevron-right"></i>
																Procurement Status <i class="fa fa-chevron-right"></i>
																Envisaged Demand.
															</strong>
														</li>
														<li class="list-group-item">Fill the details: Item
															Nomenclature, Estimated cost, Probable Date of Initiation
															and
															Remarks <i class="fa fa-chevron-right"></i> Click <strong
																class="text-success">Submit.</strong>
														</li>
														<h6 class="text-primary">To Edit Demand</h6>
														<li class="list-group-item">Click <strong
																class="text-primary"><i class="fa fa-edit"></i> Edit
																Demand</strong> <i class="fa fa-chevron-right"></i>
															<strong>Edit
																the required details</strong> <i
																class="fa fa-chevron-right"></i>
															Click <strong class="text-success">Submit.</strong>
														</li>
														<h6 class="text-primary">To inactive demand</h6>
														<li class="list-group-item">Click <strong class="text-danger"><i
																	class="fa fa-times"></i>
																Demand Inactive</strong>.
														</li>

													</ul>
												</div>
											</div>
										</div>
										<!-- Update Bulk Demand -->
										<div class="card border-danger mb-4">
											<div class="card-header card-heading card-hover text-danger"
												data-toggle="collapse" data-target="#update-bulk-demand"
												aria-expanded="true" aria-controls="update-bulk-demand">
												<span> <i class="fa fa-upload me-2"></i> Upload Bulk
													Demand
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div class="collapse " id="update-bulk-demand">
												<div class="card-body">
													<ul class="list-group list-group-numbered">
														<div class="alert alert-info" role="alert">
															<i class="fa fa-info-circle me-2"></i>
															At the top right corner, you
															can select the project for which you want to create the
															procurement.
														</div>
														<li class="list-group-item">Click <strong class="text-success">
																Procurement <i class="fa fa-chevron-right"></i>
																Procurement Status <i class="fa fa-chevron-right"></i>
																<i class="fa fa-file-excel-o"></i>
																Upload.
															</strong>
														</li>
														<li class="list-group-item">Click <strong
																class="text-success"><i class="fa fa-download"></i>
																Sample Document </strong> at the top right corner to
															download the
															Excel template. <br> Fill in the required demand details
															in the exact format provided-do not add or remove any
															columns.
															<br> Make sure the Demand Date is entered in the format
															DD-MM-YYYY. <br> Once completed, use the Choose file
															button to select your file. You will be shown a preview of
															the
															data-review it carefully and Click <strong
																class="text-success">Submit
															</strong> to finalize the upload.
														</li>
													</ul>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<!-- Ending line of Procurement Content -->

							<!-- Starting line of CARS Content -->
							<div id="CARS" class="content-section">
								<div class="card border-warning">
									<div class="card-header bg-warning text-dark">
										<h5 class="ml-3">
											<i class="fa fa-book"></i> CARS User Guide
										</h5>
									</div>
									<div class="card-body">
										<!-- RSQR FLow -->
										<div class="card border-primary mb-4">
											<div class="card-header card-heading card-hover text-primary"
												data-toggle="collapse" data-target="#rsqr-flow" aria-expanded="true"
												aria-controls="rsqr-flow">
												<span> <i class="fa fa-tasks me-2"></i> RSQR(Research
													Service Qualitative Requirement ) Flow
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div id="rsqr-flow" class="collapse show">
												<div class="card-body">
													<ol class="list-group list-group-numbered">
														<li class="list-group-item">Click <strong
																class="text-primary">CARS</strong> <i
																class="fa fa-chevron-right"></i> <strong>Initiated
																List</strong> <i class="fa fa-chevron-right"></i>
															<strong class="text-primary">Add CARS</strong>.
														</li>
														<li class="list-group-item"><strong
																class="text-info">Enter:</strong>
															<em>CARS Title,Funds From, Amount, Duration, Aim,
																Justification,RSP Details and Principal Investigator
																Details
															</em>
														</li>
														<h6 class="text-primary">To Update any details</h6>
														<li class="list-group-item">Click <strong
																style="color: orange;">Update</strong>
															: Update any details
														</li>
														<li class="list-group-item">Click <strong
																class="text-success">Next</strong> <i
																class="fa fa-chevron-right text-success"></i> <br>
															<strong class="text-info">Enter:</strong> <em>Introduction,
																Research Overview, Objectives,Major
																Requirements,Deliverables,Milestone & Timelines ,Major
																Requirements Scope of RSP, Scope of LRDE, Success
																Criterion,
																Literature Reference(if any). <i
																	class="fa fa-chevron-right"></i>
																Click <strong class="text-success">Submit </strong>after
																entering each detail
															</em> <i class="fa fa-chevron-right"></i> Click <strong
																class="text-success">Next</strong> <i
																class="fa fa-chevron-right"></i>Enter <strong
																class="text-dark">Remarks</strong> <i
																class="fa fa-chevron-right"></i>
															Click <strong class="text-success">Forward.</strong>
															<h6 class="alert alert-danger mt-2">
																Note: User can revoke the CARS by navigating to CARS <i
																	class="fa fa-chevron-right"></i> Initiated List <i
																	class="fa fa-chevron-right"></i> Click <strong
																	class="text-danger"><i class="fa fa-undo"></i>
																	Revoke.</strong>
															</h6>
														</li>

														<div class="alert alert-info mt-2" role="alert">
															<i class="fa fa-info-circle me-2"></i>
															A notification will be sent
															to the Project Director/GD.
														</div>

														<div class="alert alert-info mt-2" role="alert">
															<i class="fa fa-info-circle me-2"></i>
															Approver will get the CARS RSQR
															PDF to view before approval and RSQR approval form to
															download
														</div>
														<li class="list-group-item">Project Director/GD will
															navigate to notification or Click <strong
																class="text-danger">CARS</strong>
															<i class="fa fa-chevron-right"></i> <strong
																class="text-danger">Approvals.</strong>
														</li>
														<li class="list-group-item">Click <strong class="text-info"><i
																	class="fa fa-file-text"></i>
																RSQR </strong>: To Download <strong
																class="text-info">RSQR</strong>
															document.
														</li>
														<li class="list-group-item">Click <strong
																class="text-success"><i class="fa fa-file-text"></i>
																RSQR Approval Download </strong>: To Download <strong
																class="text-success">RSQR</strong> approval document.
														</li>
														<li class="list-group-item">Click <strong
																class="text-primary"><i class="fa fa-eye"></i>
																Preview</strong> <i class="fa fa-chevron-right"></i>
															<strong class="text-danger">Recommending Officer </strong>
															can <strong class="text-success">Recommend</strong> or
															<strong class="text-danger">Return.</strong>
														</li>
														<div class="alert alert-info mt-2" role="alert">
															<i class="fa fa-info-circle me-2"></i>
															Once <strong>RSQR Approved </strong>. A notification will be
															sent to <strong class="text-danger">DP&C.</strong>
															</h6>
														</div>

														<li class="list-group-item">DP&C will navigate to <strong
																class="text-primary">CARS</strong> <i
																class="fa fa-chevron-right"></i>
															<strong class="text-primary">All
																CARS.</strong>
														</li>
														<li class="list-group-item">Click <strong
																class="text-primary"><i class="fa fa-eye"></i>
																Preview</strong>: Preview RSQR Approval Details
														</li>
														<li class="list-group-item">Click <strong
																class="text-success"><i class="fa fa-file-text"></i>
																RSQR</strong>:Download RSQR Details
														</li>
														<li class="list-group-item">Click <strong class="text-info"><i
																	class="fa fa-calendar"></i>
																Summary Of Offer Inv</strong>: Select the Date on which
															the SoO
															Invitation should be sent.
														</li>
														<li class="list-group-item">Click <strong
																class="text-success"><i class="fa fa-folder-open"></i>
																Inv for SoO</strong>: Download Summary of Offer
														</li>
														<li class="list-group-item">Click <strong class="text-danger"><i
																	class="fa fa-file-text"></i>
																Pro form</strong>: Download Summary Offer of Provision
															of Research
															Services
														</li>

														<h6 class="alert alert-danger mt-2">DP&C will download the RSQR
															file,SoO Invitation,Pro form documents, and send them to the
															respective institution for completion.</h6>
														<li class="list-group-item">Initiated person will Navigate
															to <strong class="text-primary">CARS</strong> <i
																class="fa fa-chevron-right"></i> <strong
																class="text-primary">Initiated
																List</strong> <i class="fa fa-chevron-right"></i>
															<strong class="text-primary">Details</strong> <i
																class="fa fa-chevron-right"></i> <strong
																class="text-primary">SOC</strong>

														</li>
														<li class="list-group-item"><strong class="text-info">Fill
																the details:</strong> <em>Upload Summary,Upload
																Feasibility
																Report, Execution Plan,Amount,Alignment with,
																Justification
																for time reasonability,Justification for Cost
																reasonability,Justification for Selection of RSP,
																Success/Acceptance Criterion,RSP's offer Ref,RSP's offer
																Date,Key Professional-1 Details, Key Professional-2
																Details,Research Consultants,DRDO-Owned
																Equipment,Expenditure
																on items. <i class="fa fa-chevron-right"></i> Click
																<strong class="text-success">Submit.</strong> <i
																	class="fa fa-chevron-right"></i> Click <strong
																	class="text-success">Next.</strong>
															</em></li>
														<h6 class="alert alert-danger mt-2">In Final RSQR, you can
															update any
															detail, but Milestone and Timelines section is mandatory to
															update.</h6>
														<li class="list-group-item">Navigate to <strong
																class="text-danger">Final RSQR
															</strong>,Update Milestone and
															Timelines Enter: <em>Percentage and amount each Milestone
																requires.</em> <i class="fa fa-chevron-right"></i> SOC
															Forward <i class="fa fa-chevron-right"></i> Enter
															<strong>Remark</strong>
															<i class="fa fa-chevron-right"></i> Click <strong
																class="text-success">Forward.</strong>
														</li>


														<li class="list-group-item">

															<h6 class="text-danger">Notification will be sent to
																Project Director/GD.</h6> Project Director/GD will
															navigate to
															notification or Click <strong class="text-primary">CARS
																<i class="fa fa-chevron-right"></i> Approvals.
															</strong>
														</li>
														<li class="list-group-item">Click <strong
																class="text-primary"><i class="fa fa-file-text"></i>
																RSQR download </strong>: To Download <strong
																class="text-primary">RSQR</strong>
															document.
														</li>
														<li class="list-group-item">Click <strong
																class="text-success"><i class="fa fa-file-text"></i>
																Download </strong>: Download <strong
																class="text-success">RSQR</strong>
															SOC document.
														</li>
														<li class="list-group-item">Click <strong class="text-info"><i
																	class="fa fa-eye"></i> Preview</strong> <i
																class="fa fa-chevron-right"></i> <strong
																class="text-danger">Approving
																Officer </strong> can <strong
																class="text-success">Approve</strong> or
															<strong class="text-danger">Return.</strong>

														</li>
													</ol>
												</div>
											</div>
										</div>
										<!-- Constitute Committee for CARS -->
										<div class="card border-warning mb-4 ">
											<div class="card-header card-heading card-hover text-dark"
												data-toggle="collapse" data-target="#constitute-committee-cars"
												aria-expanded="true" aria-controls="constitute-committee-cars">
												<span> <i class="fa fa-users me-2"></i> Constitute
													Committee for CARS
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div id="constitute-committee-cars" class="collapse">
												<div class="card-body ">
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click <strong
																class="text-success">CARS</strong> <i
																class="fa fa-chevron-right"></i> <strong
																class="text-success">Initiated
																List</strong> <i class="fa fa-chevron-right"></i><strong
																class="text-success">Click Constitute Members</strong>
														</li>
														<li class="list-group-item">Add Members <i
																class="fa fa-chevron-right"></i>
															Select <i class="fa fa-chevron-right"></i> Click <strong
																class="text-success">Submit</strong></li>
														<li class="list-group-item text-success">Update <strong>Serial
																No List.</strong>
															<div class="alert alert-info" role="alert">
																<i class="fa fa-info-circle me-2"></i>
																Kindly update the
																serial number list to ensure that the employee order is
																accurately reflected in the reports.
															</div>
														</li>
														<div class="alert alert-danger mt-2" role="alert">
															<i class="fa fa-info-circle me-2"></i>
															If Committee is Not Pre-Approved Follow these Additional
															Steps
														</div>
														<li class="list-group-item">Click <strong
																class="text-primary">Preview</strong><i
																class="fa fa-chevron-right"></i>
															Select <strong class="text-success">Recommending
																Officer</strong> and
															<strong class="text-success">Approving Officer.</strong>
															<h6 class="alert alert-info mt-2">Note: One Approving Officer and
																Recommending Officer Mandatory.</h6>
														</li>
														<li class="list-group-item">Click <strong
																class="text-danger">Update</strong> <i
																class="fa fa-chevron-right"></i> <strong
																class="text-success">Forward</strong>
															to Recommending Officer.
														</li>
														<div class="alert alert-info mt-2" role="alert">
															<i class="fa fa-info-circle me-2"></i>
															Note:
															If Recommending Officer has not recommended ,the user can
															revoke the request.
														</div>
														<li class="list-group-item"><strong
																class="text-danger">Recommending
																Officer </strong> can <strong
																class="text-success">Recommend</strong>
															or <strong class="text-danger">Return.</strong></li>
														<li class="list-group-item"><strong
																class="text-primary">Approving
																Officer </strong> can <strong
																class="text-success">Approve</strong> or
															<strong class="text-danger">Return.</strong>
														</li>

													</ul>
												</div>
											</div>
										</div>
										<!-- MOM Upload -->
										<div class="card border-success mb-4 ">
											<div class="card-header card-heading card-hover text-success"
												data-toggle="collapse" data-target="#mom-upload-cars"
												aria-expanded="true" aria-controls="mom-upload-cars">
												<span> <i class="fa fa-upload me-2"></i> MOM Upload
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div id="mom-upload-cars" class="collapse">
												<div class="card-body ">
													<ul class="list-group list-group-numbered">
														<div class="alert alert-info" role="alert">
															<i class="fa fa-info-circle me-2"></i>
															Once the meeting is completed,the
															Minutes of Meeting will be uploaded to the designated
															location.
														</div>

														<li class="list-group-item">Navigate to <strong
																class="text-success">CARS</strong> <i
																class="fa fa-chevron-right"></i>
															<strong class="text-success">
																Details</strong> <i
																class="fa fa-chevron-right"></i><strong
																class="text-success">Mom Upload</strong>.
														</li>
													</ul>
												</div>
											</div>

										</div>
										<!-- SOC Flow -->
										<div class="card border-warning mb-4">
											<div class="card-header card-heading card-hover text-dark"
												data-toggle="collapse" data-target="#soc-flow" aria-expanded="true"
												aria-controls="soc-flow">
												<span> <i class="fa fa-pencil-square-o text-dark"> </i>SOC(Statement
													of Case) Flow
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div class="collapse" id="soc-flow">

												<div class="card-body ">
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">DP&C will navigate to <strong
																class="text-primary">CARS</strong> <i
																class="fa fa-chevron-right"></i>
															<strong class="text-primary">All
																CARS</strong> <i class="fa fa-chevron-right"></i>
															<strong class="text-primary">SOC.</strong>
														</li>
														<li class="list-group-item">Click on <strong
																class="text-success">Details of any
																SOC record</strong> <i class="fa fa-chevron-right"></i>
															<strong class="text-success">
																Enter SOC details</strong> <br> <em> Introduction,
																Expenditure Head, Additional Points (if any). </em> <i
																class="fa fa-chevron-right"></i> Click <strong
																class="text-success">Submit</strong>.
														</li>

														<li class="list-group-item">Go to next Tab <i
																class="fa fa-chevron-right"></i>
															<strong class="text-success">SOC
																Forward.</strong>
														</li>
														<li class="list-group-item"><strong class="text-danger">Approval
																Flow For DP&C SoC Forward</strong></li>
														<li class="list-group-item"><strong class="text-success">GH-DP&C
																<i class="fa fa-chevron-right"></i> GD-DP&C <i
																	class="fa fa-chevron-right"></i> PD/GD <i
																	class="fa fa-chevron-right"></i> MMFD AG <i
																	class="fa fa-chevron-right"></i> GD DF&M <i
																	class="fa fa-chevron-right"></i>
															</strong></li>
														<li class="list-group-item"><strong class="text-danger">
																If Amount is </strong>
															<ul class="list-group list-group-numbered">
																<li class="list-group-item">
																	<= 10,00,000 <i class="fa fa-chevron-right"></i>
																		<strong class="text-danger">Director</strong>.
																</li>
																<li class="list-group-item">>10,00,000 and Amount <
																		50,00,000 <i class="fa fa-chevron-right"> </i>
																		<strong class="text-danger">IFA O/o
																			DG(ECS)</strong> <i
																			class="fa fa-chevron-right"></i> <strong
																			class="text-danger">Director</strong>.
																</li>
																<li class="list-group-item">> 50,00,000 and Amount <=
																		3,00,00,000 <i class="fa fa-chevron-right"></i>
																		<strong class="text-danger">Director</strong> <i
																			class="fa fa-chevron-right"></i> <strong
																			class="text-danger">IFA</strong>
																		<i class="fa fa-chevron-right"></i> <strong
																			class="text-danger">DG</strong>.
																</li>
																<li class="list-group-item">> 3,00,00,000 <i
																		class="fa fa-chevron-right"></i><strong
																		class="text-danger">Director</strong>
																	<i class="fa fa-chevron-right"></i> <strong
																		class="text-danger">JSA</strong> <i
																		class="fa fa-chevron-right"></i> <strong
																		class="text-danger">SECY</strong>.
																</li>
															</ul>
														</li>
														<div class="alert alert-info mt-2" role="alert">
															<i class="fa fa-info-circle me-2"></i>
															In flow if there is IFA and
															JSA those officers are external officers. Hence on behalf of
															them, DP & C officers himself will approve on obtaining
															manual
															approval from them.
														</div>

														<li class="list-group-item">After approval flow is
															completed All the docs are available to download in <strong
																class="text-success">All Docs</strong> tab.
														</li>

													</ul>
												</div>
											</div>
										</div>
										<!-- CARS Flow -->
										<div class="card border-danger mb-4 ">
											<div class="card-header card-heading card-hover text-danger"
												data-toggle="collapse" data-target="#cars-flow" aria-expanded="true"
												aria-controls="cars-flow">
												<span> <i class="fa fa-file-text"></i> CARS (Contract for
													Acquiring Research Services) Flow
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div class="collapse" id="cars-flow">
												<div class="card-body ">
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">DP&C will navigate to <strong
																class="text-danger">CARS</strong> <i
																class="fa fa-chevron-right"></i>
															<strong class="text-danger">All
																CARS</strong> <i class="fa fa-chevron-right"></i>
															<strong class="text-danger">CARS-03.</strong>
														</li>
														<li class="list-group-item">Click on <strong
																class="text-danger">Details</strong> under action of any
															CARS
															record <br> <strong class="text-info">Enter the
																details:</strong> <br> <em> Contract Date, T0 Date, T0
																Remarks </em>
														</li>
														<div class="alert alert-info mt-2" role="alert">
															<i class="fa fa-info-circle me-2"></i>
															DP&C will download the CARS-03
															word and pdf, and send them to the respective institution.
														</div>
														<li class="list-group-item ">Click on <strong
																class="text-success">other
																Doc</strong> after updating
															contract details.
														</li>
														<li class="list-group-item">Click on <strong
																class="text-primary">CONTRACT
																SIGNATURE BUTTON</strong> <br>
															<strong class="text-primary">Fill the details such as
																Date,Flag-A,Flag-B and Flag-C.</strong>
														</li>
														<li class="list-group-item">Get the contract signed and
															approved and upload related documents in <strong
																class="text-success"> Doc
																Upload tab. </strong>
															<h6 class="alert alert-danger mt-2">Note: Approval flow is
																auto-generated and shown below.</h6>
														</li>
														<li class="list-group-item">Click on details action of any
															payment record <br> <strong class="text-primary">Fill
																the details such as Date,Flag-A,Flag-B and
																Flag-C,Invoice No
																and Invoice Date.</strong>
														</li>
														<li class="list-group-item">Navigate to payment approval
															page. Revert option also available if forwarded record is
															not
															yet approved by the next approver
															<div class="alert alert-info mt-2" role="alert">
																<i class="fa fa-info-circle me-2"></i>
																Only GD DP & C is able to
																forward the payment for approval.
															</div>
														</li>
														<li class="list-group-item">Upload related documents in <strong
																class="text-success"> Doc Upload tab. </strong>
														</li>
														<li class="list-group-item">Now in action list one more
															button added that is <strong
																class="text-success">Date</strong>.
															Click on it and update the Payment Date.
														</li>
														<li class="list-group-item">After updating the payment
															date, payment letter can be downloaded.</li>
													</ul>
												</div>
											</div>
										</div>

									</div>
								</div>
							</div>
							<!-- Ending line of CARS Content -->

							<!-- Starting line of WorkRegister Content -->
							<div id="WorkRegister" class="content-section">
								<div class="card border-secondary">
									<div class="card-header bg-secondary text-white">
										<h5 class="ml-3">
											<i class="fa fa-list-alt"></i> Work Register
										</h5>
									</div>
									<div class="card-body">
										<!-- Update Work-Register -->
										<div class="card border-secondary mb-4">
											<div class="card-header card-heading card-hover text-secondary"
												data-toggle="collapse" data-target="#update-work-register"
												aria-expanded="true" aria-controls="update-work-register">
												<span> <i class="fa fa-wrench me-2"></i> Update Work
													Register
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div class="collapse-show" id="update-work-register">
												<div class="card-body">
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click <strong
																class="text-success">Work Register. <i
																	class="fa fa-chevron-right"></i> Work Register
																Entry
															</strong></li>
														<li class="list-group-item"><em>Select: Activity
																Type(Technical, Managerial, Administrative, Support or
																Others), Project (if it is a project activity select
																Project
																else select General) from the dropdown list, Assigner of
																the
																work, Keyword from the dropdown list keyword is a
																generic
																one, select a closest keyword to the work if no keyword
																related is found, select others, Enter Work Done (max
																100
																characters), Select Work Done On (Full Day, FN, AN ) <i
																	class="fa fa-chevron-right"></i> Click <strong
																	class="text-success">Submit.</strong>
															</em></li>
														<h6 class="text-primary">To Edit</h6>
														<li class="list-group-item">Click <strong
																class="text-primary"><i class="fa fa-edit"> Edit
																</i></strong> <i class="fa fa-chevron-right"></i> Edit
															the necessary
															details <i class="fa fa-chevron-right"></i> Click <strong
																class="text-success">Update.</strong>
														</li>
														<strong class="text-primary">To add additional
															activity </strong>
														<li class="list-group-item">To add additional activity
															Click on <strong class="text-success">Add New
																Activity Row</strong>.
														</li>
														<div class="alert alert-info mt-2" role="alert">
															<i class="fa fa-info-circle me-2"></i>
															Note: Work Register cannot be filled for future dates. <br>
															Entries can be added for the past 15 days from the current
															date.
														</div>
													</ul>
												</div>
											</div>
										</div>

										<!-- Work-Register View -->
										<div class="card border-primary mb-4">
											<div class="card-header card-heading card-hover text-primary"
												data-toggle="collapse" data-target="#work-register-view"
												aria-expanded="true" aria-controls="work-register-view">
												<span> <i class="fa fa-eye me-2"></i> Work Register - View
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div class="collapse" id="work-register-view">
												<div class="card-body">
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click <strong
																class="text-success">Work Register <i
																	class="fa fa-chevron-right">.</i> Work Register View
															</strong>
														</li>
														<h6 class="text-primary">Weekly View</h6>
														<li class="list-group-item">Click on the <strong
																class="text-primary">Weekly
																View</strong> tab. In the top
															corner, select the Start Date of the week and the End Date
															of
															the week. The system will display all work done within the
															selected week.
														</li>
														<h6 class="text-primary">Monthly View</h6>
														<li class="list-group-item">Click on the <strong
																class="text-primary">Monthly
																View</strong> tab. Select the
															desired month . The system will display a summary of all
															work
															completed during that month. To save or share the report:
															Click the <strong class="text-primary"><i
																	class="fa fa-file-pdf-o"></i> PDF
																report</strong> button to export the
															report in PDF format. Click the <strong
																class="text-success"><i
																	class="fa fa-file-excel-o"></i></i> Excel
																report</strong> button to
															export
															it in Excel format.
														</li>
														<h6 class="text-primary">Periodic View</h6>
														<li class="list-group-item">Click on the <strong
																class="text-primary">Periodic
																View</strong> tab. Select the
															From Date and To Date. The system will display all work done
															within the specified period. To save or share the report:
															Click the <strong class="text-primary"><i
																	class="fa fa-file-pdf-o"></i> PDF
																report</strong> button to export the
															report in PDF format. Click the <strong
																class="text-success"><i
																	class="fa fa-file-excel-o"></i></i> Excel
																report</strong> button to
															export
															it in Excel format.
														</li>
													</ul>
												</div>
											</div>
										</div>
										<!-- Work-Register Report -->
										<div class="card border-info mb-4">
											<div class="card-header card-heading card-hover text-info"
												data-toggle="collapse" data-target="#work-register-report"
												aria-expanded="true" aria-controls="work-register-report">
												<span> <i class="fa fa-bar-chart"></i> Work-Register
													Report
												</span> <i class="fa fa-chevron-down"></i>
											</div>
											<div class="collapse" id="work-register-report">
												<div class="card-body">
													<ul class="list-group list-group-numbered">
														<li class="list-group-item">Click <strong
																class="text-success">Work Register <i
																	class="fa fa-chevron-right"></i> Work Register
																Report.
															</strong></li>
														<li class="list-group-item">In the "Reports" section, use
															the "Submitted Employees" tab to view who has submitted work
															done, and the "Not Submitted Employees" tab to track who
															hasn't.</li>

													</ul>
												</div>
											</div>
										</div>

									</div>
								</div>
							</div>
							<!-- Ending line of WorkRegister Content -->
						</div>
					</div>
					<!-- Back to Top Button -->
					<button onclick="scrollToTop()" id="topBtn" title="Go to top">
						<i class="fa fa-arrow-up"></i>
					</button>
					<script>
						// Show the button after scrolling down 100px
						window.onscroll = function () {
							const topBtn = document.getElementById("topBtn");
							if (document.body.scrollTop > 100
								|| document.documentElement.scrollTop > 100) {
								topBtn.style.display = "block";
							} else {
								topBtn.style.display = "none";
							}
						};

						// Smooth scroll to top
						function scrollToTop() {
							window.scrollTo({
								top: 0,
								behavior: 'smooth'
							});
						}
					</script>
					<script>
						$(function () {
							$('[data-toggle="tooltip"]').tooltip({
								container: 'body',
								boundary: 'window'
							})
						})
					</script>
					<script>
						function toggleSidebar() {
							const sidebar = document.getElementById('sidebar');
							sidebar.classList.toggle('collapsed');
						}

						// Menu click handler to switch active menu and content
						document.querySelectorAll('.menu-item').forEach(item => {
							item.addEventListener('click', e => {
								e.preventDefault();

								// Remove active from all menu items
								document.querySelectorAll('.menu-item').forEach(mi => mi.classList.remove('active'));
								// Add active to clicked menu item
								item.classList.add('active');

								// Hide all content sections
								document.querySelectorAll('.content-section').forEach(section => {
									section.classList.remove('active');
								});

								// Show the target content section if exists
								const targetId = item.getAttribute('data-target');
								const targetSection = document.getElementById(targetId);
								if (targetSection) {
									targetSection.classList.add('active');
								}
							});
						});
					</script>
				</body>

				</html>